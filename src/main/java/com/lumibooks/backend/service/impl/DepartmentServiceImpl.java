package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.department.request.DepartmentCreateRequest;
import com.lumibooks.backend.dto.department.request.DepartmentUpdateRequest;
import com.lumibooks.backend.dto.department.response.DepartmentAdminDetailResponse;
import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.dto.department.response.DepartmentSummaryResponse;
import com.lumibooks.backend.entity.Department;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.DepartmentMapper;
import com.lumibooks.backend.repository.DepartmentRepository;
import com.lumibooks.backend.service.DepartmentService;
import com.lumibooks.backend.specification.DepartmentSpecification;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de departamentos
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    // ============ Público ============

    @Override
    public List<DepartmentPublicResponse> getDepartmentsPublic(String search) {
        if (search != null && !search.isBlank()) {
            Specification<Department> spec = Specification.unrestricted();
            spec = spec.and(DepartmentSpecification.nameContains(search));
            spec = spec.and(DepartmentSpecification.hasActive(true));
            return departmentRepository.findAll(spec)
                    .stream()
                    .map(departmentMapper::toPublicResponse)
                    .toList();
        }
        return departmentRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(departmentMapper::toPublicResponse)
                .toList();
    }

    // ============ Admin ============

    @Override
    public Page<DepartmentSummaryResponse> getDepartmentsAdmin(String search, Boolean isActive, Pageable pageable) {
        Specification<Department> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(DepartmentSpecification.nameContains(search));
        }
        if (isActive != null) {
            spec = spec.and(DepartmentSpecification.hasActive(isActive));
        }

        return departmentRepository.findAll(spec, pageable)
                .map(departmentMapper::toSummaryResponse);
    }

    @Override
    public DepartmentAdminDetailResponse getDepartmentDetailAdmin(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departamento no encontrado con id: " + id));
        return departmentMapper.toAdminDetailResponse(department);
    }

    @Override
    @Transactional
    public DepartmentAdminDetailResponse createDepartment(DepartmentCreateRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                    "Ya existe un departamento con el nombre: " + request.getName());
        }
        Department department = departmentMapper.toEntity(request);
        return departmentMapper.toAdminDetailResponse(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public DepartmentAdminDetailResponse updateDepartment(Long id, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departamento no encontrado con id: " + id));

        if (request.getName() != null && departmentRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BadRequestException(
                    "Ya existe un departamento con el nombre: " + request.getName());
        }

        departmentMapper.updateEntity(department, request);
        return departmentMapper.toAdminDetailResponse(departmentRepository.save(department));
    }

}
