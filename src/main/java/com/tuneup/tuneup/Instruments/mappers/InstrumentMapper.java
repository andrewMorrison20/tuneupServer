package com.tuneup.tuneup.Instruments.mappers;

import com.tuneup.tuneup.Instruments.dtos.InstrumentDto;
import com.tuneup.tuneup.Instruments.entities.Instrument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface InstrumentMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "id", target = "id")
    Instrument toInstrument(InstrumentDto instrumentDto);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "id", target = "id")
    InstrumentDto toInstrumentDto(Instrument instrument);
}
