package com.tuneup.tuneup.address;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Value("${google.places.api.key}") // Load API key from application.properties
    private String googleApiKey;
    private final RestTemplate restTemplate;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper, RestTemplate restTemplate) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.restTemplate = restTemplate;
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


    public List<AddressDto> getAddressSuggestions(String postcode, String houseNumber) {
        String query = houseNumber + " " + postcode;
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json" +
                "?input=" + query +
                "&types=address" +
                "&components=country:GB" +
                "&key=" + googleApiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> predictions = (List<Map<String, Object>>) response.get("predictions");

        return predictions.stream().map(this::convertToAddressDto).collect(Collectors.toList());
    }


    private AddressDto convertToAddressDto(Map<String, Object> place) {
        AddressDto dto = new AddressDto();
        dto.setAddressLine1((String) place.get("description")); // Full formatted address
        dto.setPostcode(extractPostcode((String) place.get("description")));
        dto.setCity(extractCity((String) place.get("description")));
        dto.setCountry("United Kingdom");

        // Retrieve Lat/Lng for precise location
        Optional<Map<String, Object>> placeDetails = fetchPlaceDetails((String) place.get("place_id"));
        placeDetails.ifPresent(details -> {
            dto.setLatitude((Double) details.get("lat"));
            dto.setLongitude((Double) details.get("lng"));
        });

        return dto;
    }

    private Optional<Map<String, Object>> fetchPlaceDetails(String placeId) {
        String url = "https://maps.googleapis.com/maps/api/place/details/json" +
                "?place_id=" + placeId +
                "&fields=geometry/location" +
                "&key=" + googleApiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> result = (Map<String, Object>) response.get("result");

        if (result != null) {
            Map<String, Object> location = (Map<String, Object>) ((Map<String, Object>) result.get("geometry")).get("location");
            return Optional.of(location);
        }
        return Optional.empty();
    }

    private String extractPostcode(String address) {
        return address.replaceAll(".*(\\b[A-Z]{1,2}\\d[A-Z\\d]?\\s*\\d[A-Z]{2}\\b).*", "$1"); // UK Postcode format
    }

    private String extractCity(String address) {
        String[] parts = address.split(",");
        return parts.length > 1 ? parts[parts.length - 2].trim() : "";
    }
}


