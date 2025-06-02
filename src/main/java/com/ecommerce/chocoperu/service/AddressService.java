package com.ecommerce.chocoperu.service;

import com.ecommerce.chocoperu.dto.AddressDto;
import com.ecommerce.chocoperu.entity.Address;
import com.ecommerce.chocoperu.entity.User;
import com.ecommerce.chocoperu.repository.AddressRepository;
import com.ecommerce.chocoperu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<AddressDto> getUserAddresses(User user) {
        return addressRepository.findByUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AddressDto createOrUpdateAddress(AddressDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Address address = Address.builder()
                .id(dto.getId())
                .user(user)
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .postalCode(dto.getZipCode())
                .isDefault(dto.isDefault())
                .build();

        if (dto.isDefault()) {
            List<Address> existingAddresses = addressRepository.findByUserId(user.getId());
            for (Address a : existingAddresses) {
                a.setDefault(false);
            }
            addressRepository.saveAll(existingAddresses);
        }

        Address saved = addressRepository.save(address);

        return AddressDto.builder()
                .id(saved.getId())
                .userId(saved.getUser().getId())
                .street(saved.getStreet())
                .city(saved.getCity())
                .state(saved.getState())
                .country(saved.getCountry())
                .zipCode(saved.getPostalCode())
                .isDefault(saved.isDefault())
                .build();
    }

    public Address addAddress(User user, AddressDto dto) {
        Address address = Address.builder()
                .user(user)
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .postalCode(dto.getZipCode())
                .isDefault(dto.isDefault())
                .build();
        return addressRepository.save(address);
    }

    public AddressDto toDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getPostalCode())
                .isDefault(address.isDefault())
                .build();
    }

    public Optional<AddressDto> getDefaultAddressForUser(Long userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .map(address -> AddressDto.builder()
                        .id(address.getId())
                        .userId(userId)
                        .street(address.getStreet())
                        .city(address.getCity())
                        .state(address.getState())
                        .country(address.getCountry())
                        .zipCode(address.getPostalCode())
                        .isDefault(true)
                        .build()
                );
    }

}
