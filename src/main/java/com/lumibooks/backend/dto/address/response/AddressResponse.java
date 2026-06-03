package com.lumibooks.backend.dto.address.response;

import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.dto.district.response.DistrictPublicResponse;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información completa de una dirección.
 */
@Getter
@Builder
public class AddressResponse {

    private Long id;
    private DepartmentPublicResponse department;
    private ProvincePublicResponse province;
    private DistrictPublicResponse district;
    private String addressLine;
    private String reference;
    private boolean isDefault;

}