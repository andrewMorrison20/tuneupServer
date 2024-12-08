package com.tuneup.tuneup.address;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    /**
     * Create a new address.
     *
     * @param addressDto the AddressDto input.
     * @return the saved AddressDto.
     */
    @Transactional
    public AddressDto createAddress(AddressDto addressDto) {
        System.out.println("Entering method");
        Address address = addressMapper.toAddress(addressDto);
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    /**
     * Update an existing address by ID.
     *
     * @param id         the ID of the address to update.
     * @param addressDto the updated AddressDto data.
     * @return the updated AddressDto.
     */
    @Transactional
    public AddressDto updateAddress(Long id, AddressDto addressDto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));

        // Map updated fields from AddressDto to Address
        address.setAddressLine1(addressDto.getAddressLine1());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setCity(addressDto.getCity());
        address.setCountry(addressDto.getCountry());
        address.setPostcode(addressDto.getPostcode());

        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toDto(updatedAddress);
    }

    /**
     * Delete an address by ID.
     *
     * @param id the ID of the address to delete.
     */
    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found with ID: " + id);
        }
        addressRepository.deleteById(id);
    }

    /**
     * Get an address by ID.
     *
     * @param id the ID of the address to fetch.
     * @return the AddressDto.
     */
    @Transactional(readOnly = true)
    public AddressDto getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));
        return addressMapper.toDto(address);
    }

    /**
     * Get all addresses.
     *
     * @return a list of AddressDto.
     */
    @Transactional(readOnly = true)
    public List<AddressDto> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }
}
