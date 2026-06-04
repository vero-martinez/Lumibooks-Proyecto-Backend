package com.lumibooks.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.address.request.AddressCreateRequest;
import com.lumibooks.backend.dto.address.request.AddressUpdateRequest;
import com.lumibooks.backend.dto.address.response.AddressResponse;
import com.lumibooks.backend.service.AddressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de direcciones del usuario autenticado.
 */
@RestController
@RequestMapping("/api/client/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // =========== Endpoints ============

    // Retorna todas las direcciones del usuario autenticado
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses() {
        return ResponseEntity.ok(addressService.getAddresses());
    }

    // Crea una nueva dirección para el usuario autenticado
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @RequestBody @Valid AddressCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.createAddress(request));
    }

    // Actualiza una dirección existente del usuario autenticado
    @PatchMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @RequestBody @Valid AddressUpdateRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, request));
    }

    // Elimina una dirección del usuario autenticado
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    // Establece una dirección como predeterminada
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Long addressId) {
        addressService.setDefaultAddress(addressId);
        return ResponseEntity.noContent().build();
    }

}