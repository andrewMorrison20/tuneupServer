package com.tuneup.tuneup.address;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Value("${google.places.api.key}")
    private String googleApiKey;


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


    /**
     * Fetch address suggestions based on postcode and street name.
     */
    public List<AddressDto> getAddressSuggestions(String postcode, String streetName) {
        RestTemplate restTemplate = new RestTemplate();
        String query = streetName + " " + postcode;
        String url = "https://maps.googleapis.com/maps/api/geocode/json" +
                "?address=" + URLEncoder.encode(query, StandardCharsets.UTF_8) +
                "&components=country:GB" +
                "&key=" + googleApiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        if (results == null || results.isEmpty()) {
            throw new RuntimeException("No addresses found for " + query);
        }

        return results.stream()
                .map(this::convertToAddressDto)
                .collect(Collectors.toList());
    }

    private AddressDto convertToAddressDto(Map<String, Object> result) {
        AddressDto dto = new AddressDto();

        String formattedAddress = (String) result.get("formatted_address");
        if (formattedAddress != null) {
            String[] parts = formattedAddress.split(","); // Split by comma

            dto.setAddressLine1(parts.length > 0 ? parts[0].trim() : "");  // First part is usually street name
            dto.setCity(extractCityFromComponents((List<Map<String, Object>>) result.get("address_components")));
            dto.setPostcode(extractPostcode(formattedAddress));
            dto.setCountry("United Kingdom"); // Fixed country
        }

        // Extract latitude and longitude
        Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
        if (geometry != null) {
            Map<String, Object> location = (Map<String, Object>) geometry.get("location");
            if (location != null) {
                dto.setLatitude((Double) location.get("lat"));
                dto.setLongitude((Double) location.get("lng"));
            }
        }

        return dto;
    }

    /**
     * Extracts the city from address components.
     * Uses "postal_town" if available; otherwise, extracts from formatted address.
     */
    private String extractCityFromComponents(List<Map<String, Object>> addressComponents) {
        if (addressComponents == null) return "";

        for (Map<String, Object> component : addressComponents) {
            List<String> types = (List<String>) component.get("types");
            if (types.contains("postal_town")) {
                return (String) component.get("long_name");
            }
        }

        return "";
    }


    private String extractPostcode(String address) {
        return address.replaceAll(".*(\\b[A-Z]{1,2}\\d[A-Z\\d]?\\s*\\d[A-Z]{2}\\b).*", "$1");
    }

}


