package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.regions.dtos.RegionDto;
import com.tuneup.tuneup.regions.entities.Region;
import com.tuneup.tuneup.regions.mappers.RegionMapper;
import com.tuneup.tuneup.regions.repositories.RegionRepository;
import com.tuneup.tuneup.regions.services.RegionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegionServiceTests {

    @Mock RegionRepository regionRepository;
    @Mock RegionMapper regionMapper;
    @InjectMocks RegionService regionService;

    @Test
    void getRegions_WhenCached_ReturnsMappedDtos() {
        Region r = new Region();
        r.setName("London");
        r.setLatitude(51.5);
        r.setLongitude(-0.1);
        r.setCountry("UK");

        RegionDto dto = new RegionDto();
        dto.setName("London");
        dto.setLatitude(51.5);
        dto.setLongitude(-0.1);
        dto.setCountry("UK");

        when(regionRepository.findByNameContainingIgnoreCase("lon"))
                .thenReturn(List.of(r));
        when(regionMapper.toRegionDto(r)).thenReturn(dto);

        Set<RegionDto> result = regionService.getRegions("lon");

        assertEquals(1, result.size());
        assertTrue(result.contains(dto));
        verify(regionRepository).findByNameContainingIgnoreCase("lon");
        verifyNoMoreInteractions(regionRepository);
    }

    @Test
    void getRegions_WhenNotCached_FetchesFromApiAndSavesNew() {
        // 1) No cache hit
        when(regionRepository.findByNameContainingIgnoreCase("man"))
                .thenReturn(List.of());

        // 2) Fake API response: one entry with city & county & country
        Map<String,Object> address = new HashMap<>();
        address.put("city","Manchester");
        address.put("county","Greater Manchester");
        address.put("country","United Kingdom");

        Map<String,Object> entry = Map.of(
                "lat","53.48",
                "lon","-2.24",
                "address", address
        );
        List<Map<String,Object>> apiResponse = List.of(entry);

        // 3) Stub RestTemplate
        try (MockedConstruction<RestTemplate> rtMock = mockConstruction(RestTemplate.class, (rt, ctx) -> {
            when(rt.getForObject(anyString(), eq(List.class))).thenReturn(apiResponse);
        })) {
            // 4) Parent region does not exist yet
            when(regionRepository.findByName("Greater Manchester"))
                    .thenReturn(Optional.empty());
            Region parent = new Region();
            parent.setName("Greater Manchester");
            when(regionRepository.save(argThat(r ->
                    "Greater Manchester".equals(r.getName())
            ))).thenReturn(parent);

            // 5) Fetched region itself is new
            when(regionRepository.existsByName("Manchester")).thenReturn(false);

            // 6) Mapper → DTO
            when(regionMapper.toRegionDto(any(Region.class)))
                    .thenAnswer(inv -> {
                        Region rg = inv.getArgument(0);
                        RegionDto dto = new RegionDto();
                        dto.setName(rg.getName());
                        dto.setLatitude(rg.getLatitude());
                        dto.setLongitude(rg.getLongitude());
                        dto.setCountry(rg.getCountry());
                        return dto;
                    });

            // Capture the list passed to saveAll()
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Region>> captor = ArgumentCaptor.forClass(List.class);

            // 7) Execute under test
            Set<RegionDto> result = regionService.getRegions("man");

            // 8) Verify only the parent region was saved via save(...)
            verify(regionRepository).save(argThat(r ->
                    "Greater Manchester".equals(r.getName())
            ));

            // 9) Verify the new fetched region went through saveAll(...)
            verify(regionRepository).saveAll(captor.capture());
            List<Region> saved = captor.getValue();
            assertEquals(1, saved.size());
            assertEquals("Manchester", saved.get(0).getName());

            // 10) And confirm the returned DTO matches
            assertEquals(1, result.size());
            assertTrue(result.stream().anyMatch(d -> "Manchester".equals(d.getName())));
        }
    }

    @Test
    void getRegions_SkipsUnknownEntries() {
        when(regionRepository.findByNameContainingIgnoreCase("foo"))
                .thenReturn(List.of());

        Map<String, Object> addr1 = Map.of(
                "village", "Smallville",
                "county", "Smallshire",
                "country", "Britain"
        );
        Map<String, Object> entry1 = Map.of(
                "lat", "10", "lon", "20", "address", addr1
        );
        Map<String, Object> addr2 = Map.of(); // no fields → fallback Unknown
        Map<String, Object> entry2 = Map.of(
                "lat", "0", "lon", "0", "address", addr2
        );
        List<Map<String, Object>> apiResponse = List.of(entry1, entry2);

        try (MockedConstruction<RestTemplate> rtMock = mockConstruction(RestTemplate.class,
                (mock, ctx) -> when(mock.getForObject(anyString(), eq(List.class)))
                        .thenReturn(apiResponse))) {

            // For "Smallville"
            when(regionRepository.existsByName("Smallville")).thenReturn(false);
            // No parent region for village, county fallback would be "Smallshire"
            when(regionRepository.findByName("Smallshire"))
                    .thenReturn(Optional.empty());
            Region parent = new Region();
            parent.setName("Smallshire");
            when(regionRepository.save(argThat(r -> "Smallshire".equals(r.getName()))))
                    .thenReturn(parent);

            when(regionMapper.toRegionDto(any(Region.class)))
                    .thenAnswer(inv -> {
                        Region r = inv.getArgument(0);
                        RegionDto d = new RegionDto();
                        d.setName(r.getName());
                        return d;
                    });

            Set<RegionDto> result = regionService.getRegions("foo");

            // Only the first (village) entry should survive
            assertEquals(1, result.size());
            assertTrue(result.stream().anyMatch(d -> "Smallville".equals(d.getName())));
            verify(regionRepository).saveAll(anyList());
        }
    }
}
