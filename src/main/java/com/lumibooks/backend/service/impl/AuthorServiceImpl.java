package com.lumibooks.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.dto.author.request.AuthorCreateRequest;
import com.lumibooks.backend.dto.author.request.AuthorUpdateRequest;
import com.lumibooks.backend.dto.cloudinary.ImageUploadResponse;
import com.lumibooks.backend.dto.response.AuthorAdminResponse;
import com.lumibooks.backend.dto.response.AuthorPublicResponse;
import com.lumibooks.backend.dto.response.AuthorSummaryResponse;
import com.lumibooks.backend.entity.Author;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.AuthorRepository;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.service.AuthorService;
import com.lumibooks.backend.service.CloudinaryService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de autores.
 * Se encarga de manejar la lógica de negocio relacionada con los autores, tanto
 * para la administración
 * (como creación, actualización, listado con filtros y
 * activación/desactivación) como para la visualización pública
 * (listado público y detalles de autor). Se asegura de validar la unicidad del
 * nombre y apellido del autor, y
 * de manejar correctamente el estado activo/inactivo para la visibilidad
 * pública.
 */
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

        private final AuthorRepository authorRepository;

        private final ActionLogService actionLogService;
        private final CloudinaryService cloudinaryService;

        /**
         * Obtiene una página de autores con filtros opcionales de nombre y estado
         * activo/inactivo.
         * El resultado se mapea a AuthorSummaryResponse para mostrar solo la
         * información necesaria en listados.
         * 
         * @param name     Filtro opcional para buscar por nombre o apellido
         *                 (case-insensitive).
         * @param isActive Filtro opcional para buscar solo autores activos o inactivos.
         * @param pageable Información de paginación y ordenamiento.
         * @return Página de AuthorSummaryResponse con los autores que cumplen los
         *         filtros.
         */
        @Override
        @Transactional(readOnly = true)
        public Page<AuthorSummaryResponse> getAuthors(String name, Boolean isActive, Pageable pageable) {
                return authorRepository.findByFilters(name, isActive, pageable)
                                .map(author -> AuthorSummaryResponse.builder()
                                                .id(author.getId())
                                                .firstName(author.getFirstName())
                                                .lastName(author.getLastName())
                                                .profileImageUrl(author.getProfileImageUrl())
                                                .isActive(author.getIsActive())
                                                .createdAt(author.getCreatedAt())
                                                .build());
        }

        /**
         * Obtiene los detalles de un autor por su ID para la vista de administración.
         * Busca el autor en la base de datos y el resultado se mapea a
         * AuthorAdminResponse.
         * 
         * @param id identificador del autor.
         * @return detalles del autor encontrado para la vista de administración.
         */
        @Override
        @Transactional(readOnly = true)
        public AuthorAdminResponse getAuthorByIdAdmin(Long id) {
                Author author = authorRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

                return toAdminResponse(author);
        }

        /**
         * Crea un nuevo autor a partir de los datos proporcionados en el request.
         * Valida que no exista previamente un autor con el mismo nombre y apellido.
         * El autor se crea con estado activo por defecto y el resultado se mapea a
         * AuthorAdminResponse.
         * 
         * @param authorRequest datos del autor a crear.
         * @return detalles del autor creado para la vista de administración.
         */
        @Override
        @Transactional
        public AuthorAdminResponse createAuthor(AuthorCreateRequest authorRequest, MultipartFile profileImage) {

                if (authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                                authorRequest.getFirstName(), authorRequest.getLastName())) {
                        throw new BadRequestException("Ya existe un autor con ese nombre y apellido");
                }

                Author author = Author.builder()
                                .firstName(authorRequest.getFirstName())
                                .lastName(authorRequest.getLastName())
                                .biography(authorRequest.getBiography())
                                .isActive(true)
                                .build();

                if (profileImage != null && !profileImage.isEmpty()) {
                        ImageUploadResponse imageResponse = cloudinaryService.uploadImage(profileImage,
                                        "lumibooks/authors");
                        author.setProfileImageUrl(imageResponse.getSecureUrl());
                        author.setProfileImagePublicId(imageResponse.getPublicId());
                }

                Author saved = authorRepository.save(author);
                actionLogService.log(
                                ActionType.CREAR,
                                EntityType.AUTHOR,
                                saved.getId(),
                                "Creó el autor '" + saved.getFirstName() + " " + saved.getLastName() + "'");
                return toAdminResponse(saved);
        }

        /**
         * Actualiza los datos de un autor existente.
         * Valida que no exista otro autor con el mismo nombre y apellido antes de
         * realizar la actualización.
         * Los cambios realizados se guardan y el resultado se mapea a
         * AuthorAdminResponse.
         * 
         * @param id            identificador del autor.
         * @param authorRequest datos actualizados del autor.
         * @return detalles del autor actualizado para la vista de administración.
         */
        @Override
        @Transactional
        public AuthorAdminResponse updateAuthor(Long id, AuthorUpdateRequest authorRequest,
                        MultipartFile profileImage) {
                Author author = authorRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

                if (authorRequest.getFirstName() != null && authorRequest.getLastName() != null
                                && authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                                                authorRequest.getFirstName(), authorRequest.getLastName())
                                && !(author.getFirstName().equalsIgnoreCase(authorRequest.getFirstName())
                                                && author.getLastName()
                                                                .equalsIgnoreCase(authorRequest.getLastName()))) {
                        throw new BadRequestException("Ya existe un autor con ese nombre y apellido");
                }

                if (authorRequest.getFirstName() != null)
                        author.setFirstName(authorRequest.getFirstName());
                if (authorRequest.getLastName() != null)
                        author.setLastName(authorRequest.getLastName());
                if (authorRequest.getBiography() != null)
                        author.setBiography(authorRequest.getBiography());

                if (profileImage != null && !profileImage.isEmpty()) {
                        ImageUploadResponse imageResponse = cloudinaryService.replaceImage(
                                        author.getProfileImagePublicId(), profileImage, "lumibooks/authors");
                        author.setProfileImageUrl(imageResponse.getSecureUrl());
                        author.setProfileImagePublicId(imageResponse.getPublicId());
                }

                Author saved = authorRepository.save(author);
                actionLogService.log(
                                ActionType.EDITAR,
                                EntityType.AUTHOR,
                                saved.getId(),
                                "Editó el autor '" + saved.getFirstName() + " " + saved.getLastName() + "'");
                return toAdminResponse(saved);
        }

        /**
         * Cambia el estado de activación de un autor.
         * Si el autor está activo, se desactiva; si está inactivo, se activa.
         * Este cambio permite controlar la visibilidad pública del autor sin eliminar
         * su registro.
         * 
         * @param id identificador del autor.
         */
        @Override
        @Transactional
        public void toggleAuthorStatus(Long id) {
                Author author = authorRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

                author.setIsActive(!author.getIsActive());
                authorRepository.save(author);
                actionLogService.log(
                                ActionType.CAMBIAR_ESTADO,
                                EntityType.AUTHOR,
                                author.getId(),
                                "Cambió el estado del autor '" + author.getFirstName() + " " + author.getLastName()
                                                + "' a "
                                                + (author.getIsActive() ? "ACTIVO" : "INACTIVO"));
        }

        /**
         * Obtiene una página de autores activos para la vista pública.
         * Permite filtrar opcionalmente por nombre o apellido.
         * El resultado se mapea a AuthorPublicResponse para mostrar únicamente la
         * información pública del autor.
         * 
         * @param name     filtro opcional para buscar por nombre o apellido
         *                 (case-insensitive).
         * @param pageable información de paginación y ordenamiento.
         * @return página de AuthorPublicResponse con los autores activos que cumplen el
         *         filtro.
         */
        @Override
        @Transactional(readOnly = true)
        public Page<AuthorPublicResponse> getAuthorsPublic(String name, Pageable pageable) {
                return authorRepository.findByFilters(name, true, pageable)
                                .map(author -> AuthorPublicResponse.builder()
                                                .id(author.getId())
                                                .firstName(author.getFirstName())
                                                .lastName(author.getLastName())
                                                .profileImageUrl(author.getProfileImageUrl())
                                                .build());
        }

        /**
         * Obtiene los detalles públicos de un autor por su ID.
         * Solo permite visualizar autores que se encuentren activos.
         * El resultado se mapea a AuthorPublicResponse para exponer únicamente la
         * información pública del autor.
         * 
         * @param id identificador del autor.
         * @return detalles públicos del autor encontrado.
         */

        @Override
        @Transactional(readOnly = true)
        public AuthorPublicResponse getAuthorByIdPublic(Long id) {
                Author author = authorRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

                if (!author.getIsActive()) {
                        throw new ResourceNotFoundException("Autor no encontrado");
                }

                return AuthorPublicResponse.builder()
                                .id(author.getId())
                                .firstName(author.getFirstName())
                                .lastName(author.getLastName())
                                .biography(author.getBiography())
                                .profileImageUrl(author.getProfileImageUrl())
                                .build();
        }

        // Método privado para evitar repetir el mapeo
        private AuthorAdminResponse toAdminResponse(Author author) {
                return AuthorAdminResponse.builder()
                                .id(author.getId())
                                .firstName(author.getFirstName())
                                .lastName(author.getLastName())
                                .biography(author.getBiography())
                                .profileImageUrl(author.getProfileImageUrl())
                                .isActive(author.getIsActive())
                                .createdAt(author.getCreatedAt())
                                .updatedAt(author.getUpdatedAt())
                                .build();
        }

}