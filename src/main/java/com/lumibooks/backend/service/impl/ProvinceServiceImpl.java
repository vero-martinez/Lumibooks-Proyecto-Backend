package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.province.request.ProvinceCreateRequest;
import com.lumibooks.backend.dto.province.request.ProvinceUpdateRequest;
import com.lumibooks.backend.dto.province.response.ProvinceAdminDetailResponse;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;
import com.lumibooks.backend.dto.province.response.ProvinceSummaryResponse;
import com.lumibooks.backend.entity.Department;
import com.lumibooks.backend.entity.District;
import com.lumibooks.backend.entity.Province;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.ProvinceMapper;
import com.lumibooks.backend.repository.DepartmentRepository;
import com.lumibooks.backend.repository.DistrictRepository;
import com.lumibooks.backend.repository.ProvinceRepository;
import com.lumibooks.backend.service.ActionLogService;
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
    private final DistrictRepository districtRepository;
    private final ProvinceMapper provinceMapper;

    private final ActionLogService actionLogService;

    // ============ Público ============

    @Override
    public List<ProvincePublicResponse> getProvincesPublic(Long departmentId, String search) {
        Specification<Province> spec = Specification.unrestricted();
        spec = spec.and(ProvinceSpecification.hasDepartment(departmentId));

        if (search != null && !search.isBlank()) {
            spec = spec.and(ProvinceSpecification.nameContains(search));
        }

        return provinceRepository.findAll(spec, Sort.by("name").ascending())
                .stream()
                .map(provinceMapper::toPublicResponse)
                .toList();
    }
    // ============ Admin ============

    @Override
    public Page<ProvinceSummaryResponse> getProvincesAdmin(String search, Long departmentId,
            Pageable pageable) {
        Specification<Province> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(ProvinceSpecification.nameContains(search));
        }

        if (departmentId != null) {
            spec = spec.and(ProvinceSpecification.hasDepartment(departmentId));
        }

        return provinceRepository.findAll(spec, pageable)
                .map(provinceMapper::toSummaryResponse);
    }

    @Override
    public ProvinceAdminDetailResponse getProvinceDetailAdmin(Long id) {
        Province province = findProvinceOrThrow(id);
        List<District> districts = districtRepository.findByProvinceIdOrderByNameAsc(id);
        return provinceMapper.toAdminDetailResponse(province, districts);
    }

    @Override
    @Transactional
    public ProvinceSummaryResponse createProvince(ProvinceCreateRequest request) {
        Department department = findDepartmentOrThrow(request.getDepartmentId());

        if (provinceRepository.existsByNameAndDepartmentId(request.getName(), request.getDepartmentId())) {
            throw new BadRequestException(
                    "Ya existe una provincia con el nombre: " + request.getName() + " en este departamento");
        }

        Province province = provinceMapper.toEntity(request, department);

        Province saved = provinceRepository.save(province);
        actionLogService.log(ActionType.CREAR, EntityType.PROVINCE, saved.getId(),
                "Creó la provincia '" + saved.getName() + "'");
        return provinceMapper.toSummaryResponse(saved);
    }

    @Override
    @Transactional
    public ProvinceSummaryResponse updateProvince(Long id, ProvinceUpdateRequest request) {
        Province province = findProvinceOrThrow(id);

        if (provinceRepository.existsByNameAndDepartmentIdAndIdNot(
                request.getName(), province.getDepartment().getId(), id)) {
            throw new BadRequestException(
                    "Ya existe una provincia con el nombre: " + request.getName() + " en este departamento");
        }

        provinceMapper.updateEntity(province, request);

        Province saved = provinceRepository.save(province);
        actionLogService.log(ActionType.EDITAR, EntityType.PROVINCE, saved.getId(),
                "Editó la provincia '" + saved.getName() + "'");
        return provinceMapper.toSummaryResponse(saved);
    }

    // ============ Helpers privados ============

    private Province findProvinceOrThrow(Long id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provincia no encontrada con id: " + id));
    }

    private Department findDepartmentOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departamento no encontrado con id: " + id));
    }

}