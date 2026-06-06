package com.lumibooks.backend.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.address.request.AddressCreateRequest;
import com.lumibooks.backend.dto.address.request.AddressUpdateRequest;
import com.lumibooks.backend.dto.address.response.AddressResponse;
import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.dto.district.response.DistrictPublicResponse;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;
import com.lumibooks.backend.entity.Address;
import com.lumibooks.backend.entity.District;
import com.lumibooks.backend.entity.User;

/**
 * Mapper encargado de transformar entidades Address en DTOs de respuesta
 * y convertir datos de solicitud en entidades Address.
 */
@Component
public class AddressMapper {

    // ============ Entity --> Response DTO ============

    public AddressResponse toResponse(Address address) {
        District district = address.getDistrict();

        return AddressResponse.builder()
                .id(address.getId())
                .department(DepartmentPublicResponse.builder()
                        .id(district.getProvince().getDepartment().getId())
                        .name(district.getProvince().getDepartment().getName())
                        .build())
                .province(ProvincePublicResponse.builder()
                        .id(district.getProvince().getId())
                        .name(district.getProvince().getName())
                        .build())
                .district(DistrictPublicResponse.builder()
                        .id(district.getId())
                        .name(district.getName())
                        .build())
                .addressLine(address.getAddressLine())
                .reference(address.getReference())
                .isDefault(address.isDefault())
                .build();
    }

    // ============ Request DTO --> Entity =============

    public Address toEntity(AddressCreateRequest request, User user, District district) {
        return Address.builder()
                .user(user)
                .district(district)
                .addressLine(request.getAddressLine())
                .reference(request.getReference())
                .build();
    }

    public void updateEntity(AddressUpdateRequest request, Address address, District district) {
        Optional.ofNullable(district).ifPresent(address::setDistrict);
        Optional.ofNullable(request.getAddressLine()).ifPresent(address::setAddressLine);
        Optional.ofNullable(request.getReference()).ifPresent(address::setReference);
    }

}