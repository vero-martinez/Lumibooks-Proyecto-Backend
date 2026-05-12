package com.lumibooks.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.request.EditorialRequest;
import com.lumibooks.backend.dto.response.EditorialResponse;
import com.lumibooks.backend.entity.Editorial;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.EditorialRepository;
import com.lumibooks.backend.service.EditorialService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio para la gestión de editoriales.
 * Contiene la lógica de negocio para crear, obtener, actualizar
 * y cambiar el estado de las editoriales.
 */
@Service
@RequiredArgsConstructor
public class EditorialServiceImpl implements EditorialService {

    private final EditorialRepository editorialRepository;

    /**
     * Crea una nueva editorial validando que no exista otra con el mismo nombre.
     *
     * @param request datos de la editorial a crear
     * @return editorial creada en formato de respuesta
     */
    @Override
    @Transactional
    public EditorialResponse create(EditorialRequest request) {
        
        if (editorialRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new BadRequestException("Ya existe una editorial con ese nombre");
        }

        Editorial editorial = Editorial.builder()
                .nombre(request.getNombre())
                .activo(true)
                .build();

        Editorial saved = editorialRepository.save(editorial);
        return mapToResponse(saved);
    }

    /**
     * Obtiene una editorial por su identificador.
     *
     * @param id identificador de la editorial
     * @return editorial encontrada
     */
    @Override
    @Transactional(readOnly = true)
    public EditorialResponse getById(Long id) {
        
        Editorial editorial = editorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        return mapToResponse(editorial);
    }

    /**
     * Obtiene una lista paginada de editoriales aplicando filtros opcionales.
     *
     * @param nombre filtro por nombre (opcional)
     * @param activo filtro por estado activo/inactivo (opcional)
     * @param pageable configuración de paginación
     * @return página de editoriales filtradas
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EditorialResponse> getAll(String nombre, Boolean activo, Pageable pageable) {
        
        Page<Editorial> editorials = editorialRepository.findByFilters(nombre, activo, pageable);
        return editorials.map(this::mapToResponse);
    }

    /**
     * Actualiza los datos de una editorial existente.
     *
     * @param id identificador de la editorial
     * @param request nuevos datos de la editorial
     * @return editorial actualizada
     */
    @Override
    @Transactional
    public EditorialResponse update(Long id, EditorialRequest request) {
        
        Editorial editorial = editorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        if (!editorial.getNombre().equalsIgnoreCase(request.getNombre()) &&
            editorialRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new BadRequestException("Ya existe una editorial con ese nombre");
        }

        editorial.setNombre(request.getNombre());
        Editorial updated = editorialRepository.save(editorial);
        
        return mapToResponse(updated);
    }

    /**
     * Desactiva una editorial (soft delete).
     *
     * @param id identificador de la editorial
     */
    @Override
    @Transactional
    public void deactivate(Long id) {
        
        Editorial editorial = editorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        if (!editorial.getActivo()) {
            throw new BadRequestException("La editorial ya está desactivada");
        }

        editorial.setActivo(false);
        editorialRepository.save(editorial);
    }

    /**
     * Activa una editorial previamente desactivada.
     *
     * @param id identificador de la editorial
     */
    @Override
    @Transactional
    public void activate(Long id) {
        
        Editorial editorial = editorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        if (editorial.getActivo()) {
            throw new BadRequestException("La editorial ya está activa");
        }

        editorial.setActivo(true);
        editorialRepository.save(editorial);
    }

    /**
     * Convierte una entidad Editorial a su DTO de respuesta.
     *
     * @param editorial entidad a convertir
     * @return DTO de respuesta
     */
    private EditorialResponse mapToResponse(Editorial editorial) {
        return EditorialResponse.builder()
                .id(editorial.getId())
                .nombre(editorial.getNombre())
                .activo(editorial.getActivo())
                .fechaCreacion(editorial.getFechaCreacion())
                .fechaActualizacion(editorial.getFechaActualizacion())
                .build();
    }
}