package com.lumibooks.backend.service.impl;

import java.math.BigDecimal;
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
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.DistrictMapper;
import com.lumibooks.backend.repository.DistrictRepository;
import com.lumibooks.backend.repository.ProvinceRepository;
import com.lumibooks.backend.service.ActionLogService;
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

    private final ActionLogService actionLogService;

    // ============ Público ============

    @Override
    public List<DistrictPublicResponse> getDistrictsPublic(Long provinceId, String search) {
        Specification<District> spec = Specification.unrestricted();
        spec = spec.and(DistrictSpecification.hasProvince(provinceId));

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
    public Page<DistrictSummaryResponse> getDistrictsAdmin(String search, Long provinceId,
            Long departmentId, Boolean isShippingAvailable, Pageable pageable) {

        Specification<District> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(DistrictSpecification.nameContains(search));
        }

        if (provinceId != null) {
            spec = spec.and(DistrictSpecification.hasProvince(provinceId));
        }
        if (departmentId != null) {
            spec = spec.and(DistrictSpecification.hasDepartment(departmentId));
        }

        if (isShippingAvailable != null) {
            spec = spec.and(
                    DistrictSpecification.hasShippingAvailable(isShippingAvailable));
        }

        return districtRepository.findAll(spec, pageable)
                .map(districtMapper::toSummaryResponse);
    }

    @Override
    public DistrictAdminDetailResponse getDistrictDetailAdmin(Long id) {
        District district = findDistrictOrThrow(id);
        return districtMapper.toAdminDetailResponse(district);
    }

    @Override
    @Transactional
    public DistrictSummaryResponse createDistrict(DistrictCreateRequest request) {
        Province province = findProvinceOrThrow(request.getProvinceId());

        if (districtRepository.existsByNameAndProvinceId(request.getName(), request.getProvinceId())) {
            throw new BadRequestException(
                    "Ya existe un distrito con el nombre: " + request.getName() + " en esta provincia");
        }

        District district = districtMapper.toEntity(request, province);

        District saved = districtRepository.save(district);
        actionLogService.log(ActionType.CREAR, EntityType.DISTRICT, saved.getId(),
                "Creó el distrito '" + saved.getName() + "'");
        return districtMapper.toSummaryResponse(saved);
    }

    @Override
    @Transactional
    public DistrictSummaryResponse updateDistrict(Long id, DistrictUpdateRequest request) {
        District district = findDistrictOrThrow(id);

        if (request.getName() != null &&
                districtRepository.existsByNameAndProvinceIdAndIdNot(
                        request.getName(),
                        district.getProvince().getId(),
                        id)) {
            throw new BadRequestException(
                    "Ya existe un distrito con el nombre: " + request.getName() + " en esta provincia");
        }

        boolean willBeShippingAvailable = request.getIsShippingAvailable() != null
                ? request.getIsShippingAvailable()
                : district.isShippingAvailable();

        BigDecimal finalShippingCost = request.getShippingCost() != null
                ? request.getShippingCost()
                : district.getShippingCost();

        if (willBeShippingAvailable && finalShippingCost == null) {
            throw new BadRequestException(
                    "No se puede habilitar el envío sin un costo de envío");
        }

        districtMapper.updateEntity(district, request);

        District saved = districtRepository.save(district);
        actionLogService.log(ActionType.EDITAR, EntityType.DISTRICT, saved.getId(),
                "Editó el distrito '" + saved.getName() + "'");
        return districtMapper.toSummaryResponse(saved);
    }

    // ============ Helpers privados ============

    private District findDistrictOrThrow(Long districtId) {
        return districtRepository.findById(districtId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Distrito no encontrado con id: " + districtId));
    }

    private Province findProvinceOrThrow(Long provinceId) {
        return provinceRepository.findById(provinceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provincia no encontrada con id: " + provinceId));
    }
}