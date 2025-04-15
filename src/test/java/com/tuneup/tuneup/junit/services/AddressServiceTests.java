package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.address.dtos.AddressDto;
import com.tuneup.tuneup.address.entities.Address;
import com.tuneup.tuneup.address.mappers.AddressMapper;
import com.tuneup.tuneup.address.repositories.AddressRepository;
import com.tuneup.tuneup.address.services.AddressService;
import com.tuneup.tuneup.tuitions.entities.Tuition;
import com.tuneup.tuneup.tuitions.repositories.TuitionRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.profiles.entities.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTests {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private TuitionRepository tuitionRepository;
    @InjectMocks
    @Spy
    private AddressService addressService; // use spy for partial stubbing

    private AddressDto addressDto;
    private Address address;

    @BeforeEach
    void setUp(){
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

        ReflectionTestUtils.setField(addressService, "googleApiKey", "dummyKey");
    }

    @Test
    void testsCreateAddressReturnsAddressDto(){
        when(addressMapper.toAddress(addressDto)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDto);
        AddressDto result = addressService.createAddress(addressDto);
        assertNotNull(result);
        assertEquals(addressDto, result);
    }

    @Test
    void testUpdateAddressReturnsAddressDto(){
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDto);
        AddressDto result = addressService.updateAddress(1L, addressDto);
        assertNotNull(result);
        assertEquals(addressDto, result);
    }

    @Test
    void testUpdateAddressExpectsException(){
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> addressService.updateAddress(1L, addressDto));
    }

    @Test
    void testDeleteAddressSuccess(){
        when(addressRepository.existsById(1L)).thenReturn(true);
        doNothing().when(addressRepository).deleteById(1L);
        assertDoesNotThrow(() -> addressService.deleteAddress(1L));
        verify(addressRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAddressExpectsException(){
        when(addressRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> addressService.deleteAddress(1L));
    }

    @Test
    void testGetAddressByIdReturnsAddressDto(){
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toDto(address)).thenReturn(addressDto);
        AddressDto result = addressService.getAddressById(1L);
        assertNotNull(result);
        assertEquals(addressDto, result);
    }

    @Test
    void testGetAddressByIdExpectsException(){
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> addressService.getAddressById(1L));
    }

    @Test
    void testGetAllAddressesReturnsDtoList(){
        List<Address> addresses = Arrays.asList(address);
        List<AddressDto> addressDtos = Arrays.asList(addressDto);
        when(addressRepository.findAll()).thenReturn(addresses);
        when(addressMapper.toDto(address)).thenReturn(addressDto);
        List<AddressDto> result = addressService.getAllAddresses();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(addressDtos, result);
    }

    @Test
    void testGetAddressSuggestionsReturnsList(){
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("formatted_address", "123 Test Street, Test City, TX1 1TX, UK");
        List<Map<String, Object>> addressComponents = new ArrayList<>();
        Map<String, Object> component = new HashMap<>();
        component.put("types", Arrays.asList("postal_town"));
        component.put("long_name", "Test City");
        addressComponents.add(component);
        resultMap.put("address_components", addressComponents);
        Map<String, Object> geometry = new HashMap<>();
        Map<String, Object> location = new HashMap<>();
        location.put("lat", 55.0);
        location.put("lng", -3.0);
        geometry.put("location", location);
        resultMap.put("geometry", geometry);
        response.put("results", Arrays.asList(resultMap));
        try (MockedConstruction<RestTemplate> mocked =
                     mockConstruction(org.springframework.web.client.RestTemplate.class, (mock, context) -> {
                         when(mock.getForObject(anyString(), eq(Map.class))).thenReturn(response);
                     })) {
            List<AddressDto> suggestions = addressService.getAddressSuggestions("TX1 1TX", "Test Street");
            assertNotNull(suggestions);
            assertFalse(suggestions.isEmpty());
            AddressDto dto = suggestions.get(0);
            assertEquals("123 Test Street", dto.getAddressLine1());
            assertEquals("Test City", dto.getCity());
            assertEquals("TX1 1TX", dto.getPostcode());
            assertEquals("United Kingdom", dto.getCountry());
            assertEquals(55.0, dto.getLatitude());
            assertEquals(-3.0, dto.getLongitude());
        }
    }

    @Test
    void testGetAddressSuggestionsNoResults(){
        Map<String, Object> response = new HashMap<>();
        response.put("results", new ArrayList<>());
        try (MockedConstruction<org.springframework.web.client.RestTemplate> mocked =
                     mockConstruction(org.springframework.web.client.RestTemplate.class, (mock, context) -> {
                         when(mock.getForObject(anyString(), eq(Map.class))).thenReturn(response);
                     })) {
            assertThrows(RuntimeException.class, () -> addressService.getAddressSuggestions("TX1 1TX", "Test Street"));
        }
    }

    @Test
    void testGetAddressByTuitionIdReturnsAddressDto(){
        Long tuitionId = 100L;
        Profile tutor = new Profile();
        tutor.setId(200L);
        Tuition tuition = new Tuition();
        tuition.setTutor(tutor);
        when(tuitionRepository.findById(tuitionId)).thenReturn(Optional.of(tuition));
        Address lessonAddress = new Address();
        lessonAddress.setId(5L);
        when(addressRepository.findByProfileId(200L)).thenReturn(Optional.of(lessonAddress));
        AddressDto lessonAddressDto = new AddressDto();
        lessonAddressDto.setId(5L);
        when(addressMapper.toDto(lessonAddress)).thenReturn(lessonAddressDto);
        AddressDto result = addressService.getAddressByTuitionId(tuitionId);
        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void testGetAddressByTuitionIdInvalidTuition(){
        Long tuitionId = 100L;
        when(tuitionRepository.findById(tuitionId)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> addressService.getAddressByTuitionId(tuitionId));
    }

    @Test
    void testGetAddressByTuitionIdNoAddress(){
        Long tuitionId = 100L;
        Profile tutor = new Profile();
        tutor.setId(200L);
        Tuition tuition = new Tuition();
        tuition.setTutor(tutor);
        when(tuitionRepository.findById(tuitionId)).thenReturn(Optional.of(tuition));
        when(addressRepository.findByProfileId(200L)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> addressService.getAddressByTuitionId(tuitionId));
    }

    @Test
    void testGetMatchingDuplicateAddressFound(){
        Address duplicate = new Address();
        duplicate.setAddressLine1("122 Thornhill");
        duplicate.setPostcode("BT5 6BN");
        when(addressRepository.findAllByPostcode("BT5 6BN")).thenReturn(Set.of(duplicate));
        Optional<Address> result = addressService.getMatchingDuplicateAddress(addressDto);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetMatchingDuplicateAddressNotFound(){
        when(addressRepository.findAllByPostcode("BT5 6BN")).thenReturn(new HashSet<>());
        Optional<Address> result = addressService.getMatchingDuplicateAddress(addressDto);
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateOrUpdateAddressCreatesNew(){
        // For "create" branch, duplicate is not found.
        when(addressRepository.findAllByPostcode(addressDto.getPostcode())).thenReturn(new HashSet<>());
        // Let createAddress run as normal.
        when(addressMapper.toAddress(addressDto)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDto);
        AddressDto result = addressService.createOrUpdateAddress(addressDto);
        assertEquals(addressDto, result);
    }

    @Test
    void testCreateOrUpdateAddressUpdatesExisting() {
        // Prepare an "existing" address in the repository.
        Address existing = new Address();
        existing.setId(2L);
        existing.setAddressLine1("122 Thornhill");
        existing.setPostcode("BT5 6BN");
        existing.setLatitude(55.0);
        existing.setLongitude(-3.0);

        // Prepare an updated AddressDto with different latitude/longitude.
        AddressDto updatedDto = new AddressDto();
        updatedDto.setAddressLine1("122 Thornhill");
        updatedDto.setPostcode("BT5 6BN");
        updatedDto.setLatitude(60.0);
        updatedDto.setLongitude(-2.0);

        // Stub the duplicate address search (getMatchingDuplicateAddress)
        // The real method uses addressRepository.findAllByPostcode so stub that.
        when(addressRepository.findAllByPostcode(updatedDto.getPostcode())).thenReturn(Set.of(existing));

        // Stub the call in updateAddress: findById(existing.getId()) returns the existing address.
        when(addressRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

        // Simulate the updateAddress behavior:
        // updateAddress sets fields from updatedDto on the existing address and then saves.
        // Create an updated instance that represents what is saved.
        Address updatedAddress = new Address();
        updatedAddress.setId(existing.getId());
        updatedAddress.setAddressLine1(updatedDto.getAddressLine1());
        updatedAddress.setPostcode(updatedDto.getPostcode());
        updatedAddress.setLatitude(updatedDto.getLatitude());
        updatedAddress.setLongitude(updatedDto.getLongitude());
        // (Other fields may be copied if needed.)

        when(addressRepository.save(existing)).thenReturn(updatedAddress);

        // Map the updated address to the DTO.
        when(addressMapper.toDto(updatedAddress)).thenReturn(updatedDto);

        AddressDto result = addressService.createOrUpdateAddress(updatedDto);
        assertEquals(updatedDto, result);
    }


    @Test
    void testCreateOrUpdateAddressReturnsExisting(){
        // Prepare an "existing" address that does not need updating.
        Address existing = new Address();
        existing.setId(3L);
        existing.setAddressLine1("122 Thornhill");
        existing.setPostcode("BT5 6BN");
        existing.setLatitude(55.0);
        existing.setLongitude(-3.0);
        AddressDto existingDto = new AddressDto();
        existingDto.setAddressLine1("122 Thornhill");
        existingDto.setPostcode("BT5 6BN");
        // Stub repository call so that duplicate exists.
        when(addressRepository.findAllByPostcode(addressDto.getPostcode())).thenReturn(Set.of(existing));
        when(addressMapper.toDto(existing)).thenReturn(existingDto);
        AddressDto result = addressService.createOrUpdateAddress(addressDto);
        assertEquals(existingDto, result);
    }
}
