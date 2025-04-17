package com.tuneup.tuneup.regions.mappers;

import com.tuneup.tuneup.regions.entities.Region;
import com.tuneup.tuneup.regions.dtos.RegionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface RegionMapper {

    @Mapping(target = "parentRegionName", expression = "java(region.getParentRegion() != null ? region.getParentRegion().getName() : null)")
    RegionDto toRegionDto(Region region);

    Region toRegion(RegionDto regionDto);
}
