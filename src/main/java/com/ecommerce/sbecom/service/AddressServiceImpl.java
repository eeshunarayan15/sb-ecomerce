package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.AddressDto;
import com.ecommerce.sbecom.exception.ResourceNotFoundException;
import com.ecommerce.sbecom.model.Address;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.repository.AddressRepository;
import com.ecommerce.sbecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
@Transactional
    @Override
    public AddressDto createAddress(UUID userId, AddressDto addressDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User"+"id"+userId));
        Address address = new Address();
        address.setAddressLine1(addressDto.getAddressLine1());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setPostalCode(addressDto.getPostalCode());
        address.setCountry(addressDto.getCountry());
        address.setUser(user);

        // 3. Save the address
        Address savedAddress = addressRepository.save(address);

        // 4. Optionally, you could also add the address to the user's list
        // (This is optional because of the bidirectional relationship)
        user.getAddresses().add(savedAddress);

        // 5. Return the saved address as DTO
        return convertToDto(savedAddress);
    }

    @Override
    public List<AddressDto> getAllAddress(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        List<Address> addresses = addressRepository.findAllByUserId(userId);
        return addresses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDto getAddressById(UUID addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        return AddressDto.builder()
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .country(address.getCountry())
                .state(address.getState())
                .city(address.getCity())
                .id(address.getId())
                .postalCode(address.getPostalCode())
                .build();
    }

    @Override
    public void deleteAddressByUserIdAndAddressId(UUID userId, UUID addressId) {
        int deletedCount = addressRepository.deleteByIdAndUserId(addressId, userId);
        if (deletedCount == 0) {
            throw new ResourceNotFoundException("Address not found or doesn't belong to user");
        }
    }

    private AddressDto convertToDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                // Add other fields if you have them in DTO
                .build();
    }
}
