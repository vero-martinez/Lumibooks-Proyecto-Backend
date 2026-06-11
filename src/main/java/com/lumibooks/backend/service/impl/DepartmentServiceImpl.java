package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.department.request.DepartmentRequest;
import com.lumibooks.backend.dto.department.response.DepartmentAdminDetailResponse;
import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.dto.department.response.DepartmentSummaryResponse;
import com.lumibooks.backend.entity.Department;
import com.lumibooks.backend.entity.Province;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.DepartmentMapper;
import com.lumibooks.backend.repository.DepartmentRepository;
import com.lumibooks.backend.repository.ProvinceRepository;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.service.DepartmentService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de departamentos.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ProvinceRepository provinceRepository;
    private final DepartmentMapper departmentMapper;

    private final ActionLogService actionLogService;

    @Override
    public List<DepartmentPublicResponse> getDepartmentsPublic(String search) {
        if (search != null && !search.isBlank()) {
            return departmentRepository.findByNameContainingIgnoreCaseOrderByNameAsc(search)
                    .stream()
                    .map(departmentMapper::toPublicResponse)
                    .toList();
        }
        return departmentRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(departmentMapper::toPublicResponse)
                .toList();
    }

    @Override
    public Page<DepartmentSummaryResponse> getDepartmentsAdmin(String search, Pageable pageable) {
        Page<Department> departments = search != null && !search.isBlank()
                ? departmentRepository.findByNameContainingIgnoreCase(search, pageable)
                : departmentRepository.findAll(pageable);

        return departments.map(departmentMapper::toSummaryResponse);
    }

    @Override
    public DepartmentAdminDetailResponse getDepartmentDetailAdmin(Long id) {
        Department department = findDepartmentOrThrow(id);
        List<Province> provinces = provinceRepository.findByDepartmentIdOrderByNameAsc(id);
        return departmentMapper.toAdminDetailResponse(department, provinces);
    }

    @Override
    @Transactional
    public DepartmentSummaryResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                    "Ya existe un departamento con el nombre: " + request.getName());
        }
        Department department = departmentMapper.toEntity(request);

        Department saved = departmentRepository.save(department);
        actionLogService.log(ActionType.CREAR, EntityType.DEPARTMENT, saved.getId(),
                "Creó el departamento '" + saved.getName() + "'");
        return departmentMapper.toSummaryResponse(saved);
    }

    @Override
    @Transactional
    public DepartmentSummaryResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = findDepartmentOrThrow(id);

        if (departmentRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BadRequestException(
                    "Ya existe un departamento con el nombre: " + request.getName());
        }

        departmentMapper.updateEntity(department, request);

        Department saved = departmentRepository.save(department);
        actionLogService.log(ActionType.EDITAR, EntityType.DEPARTMENT, saved.getId(),
                "Editó el departamento '" + saved.getName() + "'");
        return departmentMapper.toSummaryResponse(saved);
    }

    // ============ Helpers privados ============

    private Department findDepartmentOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departamento no encontrado con id: " + id));
    }

}