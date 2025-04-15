package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.address.controllers.AddressController;
import com.tuneup.tuneup.address.dtos.AddressDto;
import com.tuneup.tuneup.address.services.AddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTests {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    @Test
    void createAddress_ShouldReturnCreatedAddress() {
        AddressDto addressDto = new AddressDto();

        addressDto.setAddressLine1("122 Thornhill");
        addressDto.setAddressLine2("Gilnahirk");
        addressDto.setCity("Belfast");
        addressDto.setPostcode("BT5 6BN");
        addressDto.setId(1L);
        addressDto.setCountry("Northern Ireland");

        when(addressService.createAddress(addressDto)).thenReturn(addressDto);

        ResponseEntity<AddressDto> response = addressController.createAddress(addressDto);
        assertNotNull(response.getBody());
        assertEquals(addressDto, response.getBody());
    }

    @Test
    void getAddressById_ShouldReturnAddress() {
        AddressDto addressDto = new AddressDto();

        addressDto.setAddressLine1("122 Thornhill");
        addressDto.setAddressLine2("Gilnahirk");
        addressDto.setCity("Belfast");
        addressDto.setPostcode("BT5 6BN");
        addressDto.setId(1L);
        addressDto.setCountry("Northern Ireland");

        when(addressService.getAddressById(1L)).thenReturn(addressDto);

        ResponseEntity<AddressDto> response = addressController.getAddressById(1L);
        assertNotNull(response.getBody());
        assertEquals(addressDto, response.getBody());
    }

    @Test
    void updateAddress_ShouldReturnUpdatedAddress() {
        AddressDto addressDto = new AddressDto();

        addressDto.setAddressLine1("122 Thornhill");
        addressDto.setAddressLine2("Gilnahirk");
        addressDto.setCity("Belfast");
        addressDto.setPostcode("BT5 6BN");
        addressDto.setId(1L);
        addressDto.setCountry("Northern Ireland");

        when(addressService.updateAddress(1L, addressDto)).thenReturn(addressDto);

        ResponseEntity<AddressDto> response = addressController.updateAddress(1L, addressDto);
        assertNotNull(response.getBody());
        assertEquals(addressDto, response.getBody());
    }

    @Test
    void deleteAddress_ShouldReturnNoContent() {
        doNothing().when(addressService).deleteAddress(1L);

        ResponseEntity<Void> response = addressController.deleteAddress(1L);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void getAllAddresses_ShouldReturnListOfAddresses() {

        AddressDto addressDto1 = new AddressDto();
        AddressDto addressDto2 = new AddressDto();

        addressDto1.setAddressLine1("122 Thornhill");
        addressDto1.setAddressLine2("Gilnahirk");
        addressDto1.setCity("Belfast");
        addressDto1.setPostcode("BT5 6BN");
        addressDto1.setId(1L);
        addressDto1.setCountry("Northern Ireland");

        addressDto1.setAddressLine1("12 SeaFront");
        addressDto1.setAddressLine2("DOWN");
        addressDto1.setCity("HOLYWOOD");
        addressDto1.setPostcode("BT18 9NA");
        addressDto1.setId(2L);
        addressDto1.setCountry("Northern Ireland");

        List<AddressDto> addressList = Arrays.asList(
                addressDto1,
                addressDto2
        );
        when(addressService.getAllAddresses()).thenReturn(addressList);

        ResponseEntity<List<AddressDto>> response = addressController.getAllAddresses();
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }
    @Test
    void getAddressSuggestions_ShouldReturnListOfSuggestions() {
        String postcode = "BT5 6BN";
        String streetName = "Thornhill";

        AddressDto suggestion1 = new AddressDto();
        suggestion1.setId(1L);
        suggestion1.setAddressLine1("122 Thornhill");
        suggestion1.setCity("Belfast");
        suggestion1.setPostcode("BT5 6BN");
        suggestion1.setCountry("Northern Ireland");

        AddressDto suggestion2 = new AddressDto();
        suggestion2.setId(2L);
        suggestion2.setAddressLine1("123 Thornhill");
        suggestion2.setCity("Belfast");
        suggestion2.setPostcode("BT5 6BN");
        suggestion2.setCountry("Northern Ireland");

        List<AddressDto> suggestions = Arrays.asList(suggestion1, suggestion2);
        when(addressService.getAddressSuggestions(postcode, streetName)).thenReturn(suggestions);

        ResponseEntity<List<AddressDto>> response = addressController.getAddressSuggestions(postcode, streetName);
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(suggestions, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getLessonAddressById_ShouldReturnAddress() {
        Long tuitionId = 1L;
        AddressDto lessonAddress = new AddressDto();
        lessonAddress.setId(10L);
        lessonAddress.setAddressLine1("99 Lesson Street");
        lessonAddress.setCity("Belfast");
        lessonAddress.setPostcode("BT9 9ZZ");
        lessonAddress.setCountry("Northern Ireland");

        when(addressService.getAddressByTuitionId(tuitionId)).thenReturn(lessonAddress);

        ResponseEntity<AddressDto> response = addressController.getLessonAddressById(tuitionId);
        assertNotNull(response.getBody());
        assertEquals(lessonAddress, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
