package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.regions.dtos.RegionDto;
import com.tuneup.tuneup.regions.services.RegionService;
import com.tuneup.tuneup.regions.controllers.RegionController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionControllerTests {

    @Mock
    private RegionService regionService;

    @InjectMocks
    private RegionController regionController;

    @Test
    void getRegions_ShouldReturnRegionDtos() {
        String query = "North";

        RegionDto regionDto1 = new RegionDto();
        regionDto1.setId(1L);
        regionDto1.setName("North Region");

        RegionDto regionDto2 = new RegionDto();
        regionDto2.setId(2L);
        regionDto2.setName("Northern Territory");

        Set<RegionDto> regions = new HashSet<>();
        regions.add(regionDto1);
        regions.add(regionDto2);

        when(regionService.getRegions(query)).thenReturn(regions);

        ResponseEntity<Set<RegionDto>> response = regionController.getRegions(query);
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(regionService, times(1)).getRegions(query);
    }
}
