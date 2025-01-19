package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.address.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private AddressDto addressDto;
    private Address address;

    @BeforeEach
    void setUp() {
        addressDto = new AddressDto();

        addressDto.setAddressLine1("122 Thornhill");
        addressDto.setAddressLine2("Gilnahirk");
        addressDto.setCity("Belfast");
        addressDto.setPostcode("BT5 6BN");
        addressDto.setId(1L);
        addressDto.setCountry("Northern Ireland");

        address = new Address();

        address.setAddressLine1("122 Thornhill");
        address.setAddressLine2("Gilnahirk");
        address.setCity("Belfast");
        address.setPostcode("BT5 6BN");
        address.setId(1L);
        address.setCountry("Northern Ireland");
    }

    @Test
    void createAddress_ShouldReturnSavedAddressDto() {
        when(addressMapper.toAddress(addressDto)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        AddressDto result = addressService.createAddress(addressDto);
        assertNotNull(result);
        assertEquals(addressDto, result);
    }

    @Test
    void updateAddress_ShouldReturnUpdatedAddressDto() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        AddressDto result = addressService.updateAddress(1L, addressDto);
        assertNotNull(result);
        assertEquals(addressDto, result);
    }

    @Test
    void updateAddress_ShouldThrowExceptionIfAddressNotFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> addressService.updateAddress(1L, addressDto));
    }

    @Test
    void deleteAddress_ShouldDeleteIfExists() {
        when(addressRepository.existsById(1L)).thenReturn(true);
        doNothing().when(addressRepository).deleteById(1L);

        assertDoesNotThrow(() -> addressService.deleteAddress(1L));
        verify(addressRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAddress_ShouldThrowExceptionIfAddressNotFound() {
        when(addressRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> addressService.deleteAddress(1L));
    }

    @Test
    void getAddressById_ShouldReturnAddressDto() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        AddressDto result = addressService.getAddressById(1L);
        assertNotNull(result);
        assertEquals(addressDto, result);
    }

    @Test
    void getAddressById_ShouldThrowExceptionIfNotFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> addressService.getAddressById(1L));
    }

    @Test
    void getAllAddresses_ShouldReturnListOfAddressDtos() {
        List<Address> addresses = Arrays.asList(address);
        List<AddressDto> addressDtos = Arrays.asList(addressDto);

        when(addressRepository.findAll()).thenReturn(addresses);
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        List<AddressDto> result = addressService.getAllAddresses();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(addressDtos, result);
    }
}
