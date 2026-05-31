package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.district.request.DistrictCreateRequest;
import com.lumibooks.backend.dto.district.request.DistrictUpdateRequest;
import com.lumibooks.backend.dto.district.response.DistrictAdminDetailResponse;
import com.lumibooks.backend.dto.district.response.DistrictPublicResponse;
import com.lumibooks.backend.dto.district.response.DistrictSummaryResponse;
import com.lumibooks.backend.entity.District;
import com.lumibooks.backend.entity.Province;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.DistrictMapper;
import com.lumibooks.backend.repository.DistrictRepository;
import com.lumibooks.backend.repository.ProvinceRepository;
import com.lumibooks.backend.service.DistrictService;
import com.lumibooks.backend.specification.DistrictSpecification;

import lombok.RequiredArgsConstructor;

/**
 * Implementación de la interfaz DistrictService para la gestión de distritos.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictMapper districtMapper;

    // ============ Público ============

    @Override
    public List<DistrictPublicResponse> getDistrictsPublic(Long provinceId, String search) {
        Specification<District> spec = Specification.unrestricted();
        spec = spec.and(DistrictSpecification.hasActive(true));
        spec = spec.and(DistrictSpecification.hasProvince(provinceId));
        spec = spec.and(DistrictSpecification.hasProvinceActive());
        spec = spec.and(DistrictSpecification.hasDepartmentActive());

        if (search != null && !search.isBlank()) {
            spec = spec.and(DistrictSpecification.nameContains(search));
        }

        return districtRepository.findAll(spec)
                .stream()
                .map(districtMapper::toPublicResponse)
                .toList();
    }
    // ============ Admin ============

    @Override
    public Page<DistrictSummaryResponse> getDistrictsAdmin(String search, Boolean isActive, Long provinceId,
            Long departmentId, Pageable pageable) {
        Specification<District> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(DistrictSpecification.nameContains(search));
        }
        if (isActive != null) {
            spec = spec.and(DistrictSpecification.hasActive(isActive));
        }
        if (provinceId != null) {
            spec = spec.and(DistrictSpecification.hasProvince(provinceId));
        }
        if (departmentId != null) {
            spec = spec.and(DistrictSpecification.hasDepartment(departmentId));
        }

        return districtRepository.findAll(spec, pageable)
                .map(districtMapper::toSummaryResponse);
    }

    @Override
    public DistrictAdminDetailResponse getDistrictDetailAdmin(Long id) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Distrito no encontrado con id: " + id));
        return districtMapper.toAdminDetailResponse(district);
    }

    @Override
    @Transactional
    public DistrictAdminDetailResponse createDistrict(DistrictCreateRequest request) {
        Province province = provinceRepository.findById(request.getProvinceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provincia no encontrada con id: " + request.getProvinceId()));

        if (Boolean.TRUE.equals(request.getIsActive()) && !province.isActive()) {
            throw new BadRequestException(
                    "No se puede crear un distrito activo en una provincia inactiva");
        }

        if (districtRepository.existsByNameAndProvinceId(request.getName(), request.getProvinceId())) {
            throw new BadRequestException(
                    "Ya existe un distrito con el nombre: " + request.getName() + " en esta provincia");
        }

        District district = districtMapper.toEntity(request, province);
        return districtMapper.toAdminDetailResponse(districtRepository.save(district));
    }

    @Override
    @Transactional
    public DistrictAdminDetailResponse updateDistrict(Long id, DistrictUpdateRequest request) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Distrito no encontrado con id: " + id));

        Province province = null;
        if (request.getProvinceId() != null) {
            province = provinceRepository.findById(request.getProvinceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Provincia no encontrada con id: " + request.getProvinceId()));
        }

        Province provinciaActual = province != null ? province : district.getProvince();

        if (Boolean.TRUE.equals(request.getIsActive()) && !provinciaActual.isActive()) {
            throw new BadRequestException(
                    "No se puede activar un distrito si su provincia está inactiva");
        }

        Long provinceIdAValidar = provinciaActual.getId();
        String nameAValidar = request.getName() != null ? request.getName() : district.getName();

        if (request.getName() != null &&
                districtRepository.existsByNameAndProvinceIdAndIdNot(nameAValidar, provinceIdAValidar, id)) {
            throw new BadRequestException(
                    "Ya existe un distrito con el nombre: " + nameAValidar + " en esta provincia");
        }

        districtMapper.updateEntity(district, request, province);
        return districtMapper.toAdminDetailResponse(districtRepository.save(district));
    }

}