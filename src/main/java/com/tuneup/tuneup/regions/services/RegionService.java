package com.tuneup.tuneup.regions.services;

import com.tuneup.tuneup.regions.dtos.RegionDto;
import com.tuneup.tuneup.regions.entities.Region;
import com.tuneup.tuneup.regions.mappers.RegionMapper;
import com.tuneup.tuneup.regions.repositories.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;
    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search";

    public RegionService(RegionRepository regionRepository, RegionMapper regionMapper) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper;
    }

    /**
     * Fetches the set of regions matching the search query provided
     * @param query search params to execute search against
     * @return Set of unique matching regions
     */
    public Set<RegionDto> getRegions(String query) {
        // Step 1: Check the database cache for matching regions
        List<Region> cachedRegions = regionRepository.findByNameContainingIgnoreCase(query);
        if (!cachedRegions.isEmpty()) {
            return cachedRegions.stream().map(regionMapper::toRegionDto).collect(Collectors.toSet());
        }

        // Step 2: Fetch regions dynamically from the API
        List<Region> fetchedRegions = fetchRegionsFromApi(query);

        List<Region> newRegions = fetchedRegions.stream()
                .filter(region -> !regionRepository.existsByName(region.getName()))
                .collect(Collectors.toList());

        // Step 3: Save fetched regions to the database
        regionRepository.saveAll(newRegions);

        return fetchedRegions.stream().map(regionMapper::toRegionDto).collect(Collectors.toSet());
    }

    /**
     * Sends a request to nominatim API to retrieve location data matching a given query
     * @param query parameters to search for
     * @return List<Region> matching data formatted to Region entities
     */
    private List<Region> fetchRegionsFromApi(String query) {
        RestTemplate restTemplate = new RestTemplate();
        String url = NOMINATIM_API_URL + "?q=" + query + "&countrycodes=GB&format=json&addressdetails=1";

        List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
        List<Region> regions = new ArrayList<>();

        for (Map<String, Object> item : response) {
            Map<String, Object> address = (Map<String, Object>) item.get("address");

            // Extract primary name
            String name = getPrimaryRegionName(address);
            if (name.equals("Unknown")) {
                continue; // Skip invalid entries
            }

            // Create the region object
            Region region = new Region();
            region.setName(name);
            region.setLatitude(Double.parseDouble(item.get("lat").toString()));
            region.setLongitude(Double.parseDouble(item.get("lon").toString()));
            region.setCountry(address.get("country").toString());

            // Set parent region
            if (address.containsKey("county")) {
                Region parentRegion = getOrCreateParentRegion(address.get("county").toString());
                region.setParentRegion(parentRegion);
            }
            else if (address.containsKey("state_district")) {
                Region parentRegion = getOrCreateParentRegion(address.get("state_district").toString());
                region.setParentRegion(parentRegion);
            }
            else if (address.containsKey("district")) {
                Region parentRegion = getOrCreateParentRegion(address.get("district").toString());
                region.setParentRegion(parentRegion);
            }

            regions.add(region);
        }

        return regions;
    }

    /**
     * Derive the primary region name from the raw json returned by the API
     * @param address the raw address
     * @return Primary name as string
     */
    private String getPrimaryRegionName(Map<String, Object> address) {
        // Extract the primary region name (city or town)
        if (address.containsKey("city")) {
            return address.get("city").toString();
        } else if (address.containsKey("town")) {
            return address.get("town").toString();
        } else if (address.containsKey("village")) {
            return address.get("village").toString();
        } else {
            return address.getOrDefault("county", "Unknown").toString(); // Fallback to county if no city/town
        }
    }

    /**
     * Retrieve the parent region (or create one) for a given region
     * @param parentName
     * @return the region associated with the parentname
     */
    private Region getOrCreateParentRegion(String parentName) {
        // Check if the parent region (e.g., county) exists in the database
        return regionRepository.findByName(parentName)
                .orElseGet(() -> {
                    // Create and save the parent region if it doesn’t exist
                    Region parentRegion = new Region();
                    parentRegion.setName(parentName);
                    return regionRepository.save(parentRegion);
                });
    }
}
