package com.tuneup.tuneup.address.controllers;

import com.tuneup.tuneup.address.dtos.AddressDto;
import com.tuneup.tuneup.address.services.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This api is for creating a users residential address, this is primarily for verification and billing details once integrated
 * payments have been implemented. To add a users tuition region use the regions api
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Create a new address.
     *
     * @param addressDto AddressDto object from the request body.
     * @return ResponseEntity containing the created AddressDto.
     */
    @PostMapping
    public ResponseEntity<AddressDto> createAddress(@RequestBody AddressDto addressDto) {
        AddressDto createdAddress = addressService.createAddress(addressDto);
        return ResponseEntity.ok(createdAddress);
    }

    /**
     * Get an address by ID.
     *
     * @param id Address ID from the path variable.
     * @return ResponseEntity containing the AddressDto.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long id) {
        AddressDto addressDto = addressService.getAddressById(id);
        return ResponseEntity.ok(addressDto);
    }

    /**
     * Update an existing address by ID.
     *
     * @param id         Address ID from the path variable.
     * @param addressDto AddressDto object with updated data.
     * @return ResponseEntity containing the updated AddressDto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressDto addressDto) {
        AddressDto updatedAddress = addressService.updateAddress(id, addressDto);
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * Delete an address by ID.
     *
     * @param id Address ID from the path variable.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all addresses.
     *
     * @return ResponseEntity containing a list of AddressDto.
     */
    @GetMapping
    public ResponseEntity<List<AddressDto>> getAllAddresses() {
        List<AddressDto> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }

    /**
     * Fetch address suggestions (autocomplete using Google Places api) based on house number and postcode.
     *
     * @param postcode    The postcode input by the user.
     * @return A set of AddressDto suggestions.
     */
    @GetMapping("/lookup")
    public ResponseEntity<List<AddressDto>> getAddressSuggestions(
            @RequestParam String postcode,
            @RequestParam String streetName) {
        List<AddressDto> suggestions = addressService.getAddressSuggestions(postcode, streetName );
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get an address for a lesson by tuitionID.
     *
     * @param id tuition ID from the path variable.
     * @return ResponseEntity containing the AddressDto.
     */
    @GetMapping("/lesson/{id}/location")
    public ResponseEntity<AddressDto> getLessonAddressById(@PathVariable Long id) {
        AddressDto addressDto = addressService.getAddressByTuitionId(id);
        return ResponseEntity.ok(addressDto);
    }
}
