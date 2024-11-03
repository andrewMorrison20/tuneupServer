package com.tuneup.tuneup.users.mappers;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface AppUserMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "id", target = "id")
    AppUser toAppUser(AppUserDto appUserDto);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "id", target = "id")
    AppUserDto toAppUserDto(AppUser appUser);
}
