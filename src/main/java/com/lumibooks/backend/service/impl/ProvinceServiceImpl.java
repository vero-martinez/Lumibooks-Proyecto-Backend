package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.province.request.ProvinceCreateRequest;
import com.lumibooks.backend.dto.province.request.ProvinceUpdateRequest;
import com.lumibooks.backend.dto.province.response.ProvinceAdminDetailResponse;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;
import com.lumibooks.backend.dto.province.response.ProvinceSummaryResponse;
import com.lumibooks.backend.entity.Department;
import com.lumibooks.backend.entity.Province;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.ProvinceMapper;
import com.lumibooks.backend.repository.DepartmentRepository;
import com.lumibooks.backend.repository.ProvinceRepository;
import com.lumibooks.backend.service.ProvinceService;
import com.lumibooks.backend.specification.ProvinceSpecification;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de provincias
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;
    private final DepartmentRepository departmentRepository;
    private final ProvinceMapper provinceMapper;

    // ============ Público ============

    @Override
    public List<ProvincePublicResponse> getProvincesPublic(Long departmentId, String search) {
        if (search != null && !search.isBlank()) {
            Specification<Province> spec = Specification.unrestricted();
            spec = spec.and(ProvinceSpecification.nameContains(search));
            spec = spec.and(ProvinceSpecification.hasActive(true));
            spec = spec.and(ProvinceSpecification.hasDepartment(departmentId));
            return provinceRepository.findAll(spec)
                    .stream()
                    .map(provinceMapper::toPublicResponse)
                    .toList();
        }
        return provinceRepository.findByDepartmentIdAndIsActiveTrueOrderByNameAsc(departmentId)
                .stream()
                .map(provinceMapper::toPublicResponse)
                .toList();
    }

    // ============ Admin ============

    @Override
    public Page<ProvinceSummaryResponse> getProvincesAdmin(String search, Boolean isActive, Long departmentId, Pageable pageable) {
        Specification<Province> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(ProvinceSpecification.nameContains(search));
        }
        if (isActive != null) {
            spec = spec.and(ProvinceSpecification.hasActive(isActive));
        }
        if (departmentId != null) {
            spec = spec.and(ProvinceSpecification.hasDepartment(departmentId));
        }

        return provinceRepository.findAll(spec, pageable)
                .map(provinceMapper::toSummaryResponse);
    }

    @Override
    public ProvinceAdminDetailResponse getProvinceDetailAdmin(Long id) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provincia no encontrada con id: " + id));
        return provinceMapper.toAdminDetailResponse(province);
    }

    @Override
    @Transactional
    public ProvinceAdminDetailResponse createProvince(ProvinceCreateRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departamento no encontrado con id: " + request.getDepartmentId()));

        if (Boolean.TRUE.equals(request.getIsActive()) && !department.isActive()) {
            throw new BadRequestException(
                    "No se puede crear una provincia activa en un departamento inactivo");
        }

        if (provinceRepository.existsByNameAndDepartmentId(request.getName(), request.getDepartmentId())) {
            throw new BadRequestException(
                    "Ya existe una provincia con el nombre: " + request.getName() + " en este departamento");
        }

        Province province = provinceMapper.toEntity(request, department);
        return provinceMapper.toAdminDetailResponse(provinceRepository.save(province));
    }

    @Override
    @Transactional
    public ProvinceAdminDetailResponse updateProvince(Long id, ProvinceUpdateRequest request) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provincia no encontrada con id: " + id));

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Departamento no encontrado con id: " + request.getDepartmentId()));
        }

        Department deptoActual = department != null ? department : province.getDepartment();

        if (Boolean.TRUE.equals(request.getIsActive()) && !deptoActual.isActive()) {
            throw new BadRequestException(
                    "No se puede activar una provincia si su departamento está inactivo");
        }

        Long departmentIdAValidar = deptoActual.getId();
        String nameAValidar = request.getName() != null ? request.getName() : province.getName();

        if (request.getName() != null &&
                provinceRepository.existsByNameAndDepartmentIdAndIdNot(nameAValidar, departmentIdAValidar, id)) {
            throw new BadRequestException(
                    "Ya existe una provincia con el nombre: " + nameAValidar + " en este departamento");
        }

        provinceMapper.updateEntity(province, request, department);
        return provinceMapper.toAdminDetailResponse(provinceRepository.save(province));
    }

}
