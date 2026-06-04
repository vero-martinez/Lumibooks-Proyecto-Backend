package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.address.request.AddressCreateRequest;
import com.lumibooks.backend.dto.address.request.AddressUpdateRequest;
import com.lumibooks.backend.dto.address.response.AddressResponse;
import com.lumibooks.backend.entity.Address;
import com.lumibooks.backend.entity.District;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.AddressMapper;
import com.lumibooks.backend.repository.AddressRepository;
import com.lumibooks.backend.repository.DistrictRepository;
import com.lumibooks.backend.security.AuthenticatedUserProvider;
import com.lumibooks.backend.service.AddressService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de direcciones del usuario autenticado.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final DistrictRepository districtRepository;
    private final AddressMapper addressMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public List<AddressResponse> getAddresses() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse createAddress(AddressCreateRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        long total = addressRepository.countByUserId(user.getId());
        if (total >= 5) {
            throw new BadRequestException("No puedes tener más de 5 direcciones");
        }

        District district = resolveDistrict(request.getDistrictId());
        Address address = addressMapper.toEntity(request, user, district);

        if (total == 0) {
            address.setDefault(true);
        }

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressUpdateRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Address address = resolveAddress(addressId, user.getId());

        District district = request.getDistrictId() != null
                ? resolveDistrict(request.getDistrictId())
                : null;

        addressMapper.updateEntity(request, address, district);
        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Address address = resolveAddress(addressId, user.getId());
        boolean wasDefault = address.isDefault();

        addressRepository.delete(address);

        if (wasDefault) {
            addressRepository.findByUserId(user.getId())
                    .stream()
                    .findFirst()
                    .ifPresent(first -> {
                        first.setDefault(true);
                        addressRepository.save(first);
                    });
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Address address = resolveAddress(addressId, user.getId());

        if (address.isDefault()) {
            throw new BadRequestException("Esta dirección ya es la predeterminada");
        }

        addressRepository.clearDefaultByUserId(user.getId());
        address.setDefault(true);
        addressRepository.save(address);
    }

    // ============ Helpers privados ============

    private Address resolveAddress(Long addressId, Long userId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dirección no encontrada con id: " + addressId));
    }

    private District resolveDistrict(Long districtId) {
        return districtRepository.findByIdAndIsActiveTrue(districtId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Distrito no encontrado con id: " + districtId));
    }

}