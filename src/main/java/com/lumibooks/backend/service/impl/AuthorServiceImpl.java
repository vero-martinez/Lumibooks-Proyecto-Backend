package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.dto.author.request.AuthorCreateRequest;
import com.lumibooks.backend.dto.author.request.AuthorUpdateRequest;
import com.lumibooks.backend.dto.cloudinary.ImageUploadResponse;
import com.lumibooks.backend.dto.author.response.AuthorAdminResponse;
import com.lumibooks.backend.dto.author.response.AuthorDetailResponse;
import com.lumibooks.backend.dto.author.response.AuthorPublicResponse;
import com.lumibooks.backend.dto.author.response.AuthorSummaryResponse;
import com.lumibooks.backend.entity.Author;
import com.lumibooks.backend.mapper.AuthorMapper;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.AuthorRepository;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.service.AuthorService;
import com.lumibooks.backend.service.CloudinaryService;
import com.lumibooks.backend.specification.AuthorSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

        private final AuthorRepository authorRepository;
        private final AuthorMapper authorMapper;
        private final ActionLogService actionLogService;
        private final CloudinaryService cloudinaryService;

        @Override
        @Transactional(readOnly = true)
        public Page<AuthorSummaryResponse> getAuthors(String search, Boolean isActive, Pageable pageable) {

                Specification<Author> spec = Specification.unrestricted();

                if (search != null && !search.isBlank()) {
                        spec = spec.and(AuthorSpecification.search(search));
                }
                if (isActive != null) {
                        spec = spec.and(AuthorSpecification.hasActive(isActive));
                }

                return authorRepository.findAll(spec, pageable)
                                .map(authorMapper::toSummaryResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public AuthorAdminResponse getAuthorByIdAdmin(Long id) {
                Author author = authorRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

                return authorMapper.toAdminResponse(author);
        }

        @Override
        @Transactional
        public AuthorAdminResponse createAuthor(AuthorCreateRequest authorRequest, MultipartFile profileImage) {

                if (authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                                authorRequest.getFirstName(), authorRequest.getLastName())) {
                        throw new BadRequestException("Ya existe un autor con ese nombre y apellido");
                }

                Author author = authorMapper.toEntity(authorRequest);

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
                return authorMapper.toAdminResponse(saved);
        }

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

                authorMapper.updateEntity(authorRequest, author);

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
                return authorMapper.toAdminResponse(saved);
        }

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

        @Override
        @Transactional(readOnly = true)
        public Page<AuthorPublicResponse> getAuthorsPublic(String search, Pageable pageable) {

                Specification<Author> spec = AuthorSpecification.isActive();

                if (search != null && !search.isBlank()) {
                        spec = spec.and(AuthorSpecification.search(search));
                }

                return authorRepository.findAll(spec, pageable)
                                .map(authorMapper::toPublicResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public AuthorDetailResponse getAuthorByIdPublic(Long id) {
                Author author = authorRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

                if (!author.getIsActive()) {
                        throw new ResourceNotFoundException("Autor no encontrado");
                }

                return authorMapper.toDetailResponse(author);
        }

        @Override
        @Transactional(readOnly = true)
        public List<AuthorPublicResponse> getAllActive(String search) {
                Specification<Author> spec = AuthorSpecification.isActive();

                if (search != null && !search.isBlank()) {
                        spec = spec.and(AuthorSpecification.search(search));
                }

                return authorRepository.findAll(spec)
                                .stream()
                                .map(authorMapper::toPublicResponse)
                                .toList();
        }

}