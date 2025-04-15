package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.address.controllers.AddressController;
import com.tuneup.tuneup.address.dtos.AddressDto;
import com.tuneup.tuneup.address.services.AddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
}
