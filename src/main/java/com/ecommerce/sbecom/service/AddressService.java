package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.AddressDto;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressDto createAddress(UUID userId, AddressDto addressDto);

    List<AddressDto> getAllAddress(UUID userId);
}
