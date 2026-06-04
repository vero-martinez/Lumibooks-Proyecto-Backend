package com.lumibooks.backend.service;

import java.util.List;

import com.lumibooks.backend.dto.address.request.AddressCreateRequest;
import com.lumibooks.backend.dto.address.request.AddressUpdateRequest;
import com.lumibooks.backend.dto.address.response.AddressResponse;

/**
 * Interfaz para la gestión de direcciones del usuario autenticado.
 */
public interface AddressService {

    /**
     * Retorna todas las direcciones del usuario autenticado.
     * @return lista de direcciones con distrito, provincia y departamento
     */
    List<AddressResponse> getAddresses();

    /**
     * Crea una nueva dirección para el usuario autenticado.
     * Si es la primera dirección, se establece como predeterminada automáticamente.
     * @param request datos de la nueva dirección
     * @return dirección creada
     * @throws BadRequestException si el usuario ya tiene 5 direcciones
     * @throws ResourceNotFoundException si el distrito no existe o está inactivo
     */
    AddressResponse createAddress(AddressCreateRequest request);

    /**
     * Actualiza una dirección existente del usuario autenticado.
     * @param addressId identificador de la dirección
     * @param request campos a actualizar
     * @return dirección actualizada
     * @throws ResourceNotFoundException si la dirección o distrito no existen
     */
    AddressResponse updateAddress(Long addressId, AddressUpdateRequest request);

    /**
     * Elimina una dirección del usuario autenticado.
     * Si era la dirección predeterminada, la primera dirección restante
     * se establece como la nueva predeterminada.
     * @param addressId identificador de la dirección
     * @throws ResourceNotFoundException si la dirección no existe
     */
    void deleteAddress(Long addressId);

    /**
     * Establece una dirección como predeterminada.
     * Desmarca la dirección predeterminada anterior automáticamente.
     * @param addressId identificador de la dirección
     * @throws ResourceNotFoundException si la dirección no existe
     * @throws BadRequestException si la dirección ya es la predeterminada
     */
    void setDefaultAddress(Long addressId);

}