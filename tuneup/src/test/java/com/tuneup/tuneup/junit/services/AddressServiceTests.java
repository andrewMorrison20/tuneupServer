package com.tuneup.tuneup.junit.services;
import com.tuneup.tuneup.address.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private Address address;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize sample data
        address = new Address();
        address.setId(1L);
        address.setAddressLine1("123 Main St");
        address.setAddressLine2("Apt 4B");
        address.setCity("Springfield");
        address.setCountry("USA");
        address.setPostcode("12345");

        addressDto = new AddressDto();
        addressDto.setId(1L);
        addressDto.setAddressLine1("123 Main St");
        addressDto.setAddressLine2("Apt 4B");
        addressDto.setCity("Springfield");
        addressDto.setCountry("USA");
        addressDto.setPostcode("12345");
    }

    @Test
    void createAddress_ShouldReturnSavedAddressDto() {
        when(addressMapper.toAddress(addressDto)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        AddressDto result = addressService.createAddress(addressDto);

        assertNotNull(result);
        assertEquals(addressDto.getId(), result.getId());
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    void updateAddress_ShouldReturnUpdatedAddressDto() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        AddressDto result = addressService.updateAddress(1L, addressDto);

        assertNotNull(result);
        assertEquals(addressDto.getCity(), result.getCity());
        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    void updateAddress_ShouldThrowException_WhenAddressNotFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            addressService.updateAddress(1L, addressDto);
        });

        assertEquals("Address not found with ID: 1", exception.getMessage());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void deleteAddress_ShouldDeleteAddress() {
        when(addressRepository.existsById(1L)).thenReturn(true);
        doNothing().when(addressRepository).deleteById(1L);

        assertDoesNotThrow(() -> addressService.deleteAddress(1L));
        verify(addressRepository, times(1)).existsById(1L);
        verify(addressRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAddress_ShouldThrowException_WhenAddressNotFound() {
        when(addressRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            addressService.deleteAddress(1L);
        });

        assertEquals("Address not found with ID: 1", exception.getMessage());
        verify(addressRepository, times(1)).existsById(1L);
    }

    @Test
    void getAddressById_ShouldReturnAddressDto() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        AddressDto result = addressService.getAddressById(1L);

        assertNotNull(result);
        assertEquals(addressDto.getId(), result.getId());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void getAddressById_ShouldThrowException_WhenAddressNotFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            addressService.getAddressById(1L);
        });

        assertEquals("Address not found with ID: 1", exception.getMessage());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void getAllAddresses_ShouldReturnListOfAddressDto() {
        List<Address> addresses = Arrays.asList(address, address);
        List<AddressDto> addressDtos = Arrays.asList(addressDto, addressDto);

        when(addressRepository.findAll()).thenReturn(addresses);
        when(addressMapper.toDto(any(Address.class))).thenReturn(addressDto);

        List<AddressDto> result = addressService.getAllAddresses();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(addressRepository, times(1)).findAll();
    }
}
