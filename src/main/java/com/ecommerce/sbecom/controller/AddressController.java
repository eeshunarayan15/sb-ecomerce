package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.AddressDto;
import com.ecommerce.sbecom.dto.ApiResponse;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createAddress(@RequestBody AddressDto addressDto, Authentication authentication, HttpServletRequest request) {

        User user = (User) authentication.getPrincipal(); // ✅ cast
        assert user != null;
        UUID userId = user.getId();                        // ✅ USER ID

        System.out.println(userId);

        AddressDto address = addressService.createAddress(userId, addressDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder().data(address).success(true).message("Address created successfully").timestamp(LocalDateTime.now().toString()).build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllAddress(Authentication authentication) {

        User user = (User) authentication.getPrincipal(); // ✅ cast
        assert user != null;
        UUID userId = user.getId();                        // ✅ USER ID

        System.out.println(userId);

        List<AddressDto> allAddress = addressService.getAllAddress(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().data(allAddress).success(true).message("Address found successfully").timestamp(LocalDateTime.now().toString()).build());
    }


@GetMapping("/{addressId}")
public ResponseEntity<ApiResponse<Object>> getAddressById(@PathVariable UUID addressId, Authentication authentication) {

    AddressDto addressDto = addressService.getAddressById(addressId);

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
            .data(addressDto)
            .success(true)
            .message("Address found successfully")
            .timestamp(LocalDateTime.now().toString())
            .build());

}


  @DeleteMapping("/{addressId}")
    public ResponseEntity<Object> deleteAddressByUserIdAndAddressId(
            @PathVariable UUID addressId,
            Authentication authentication
    ) {
      User user = (User) authentication.getPrincipal(); // ✅ cast
      assert user != null;
      UUID userId = user.getId();                        // ✅ USER ID


      addressService.deleteAddressByUserIdAndAddressId(userId,addressId);

      return ResponseEntity.noContent().build();  // HTTP 204


    }
}
