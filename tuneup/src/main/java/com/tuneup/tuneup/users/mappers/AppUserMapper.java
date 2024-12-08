package com.tuneup.tuneup.users.mappers;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface AppUserMapper {


    AppUser toAppUser(AppUserDto appUserDto);

    AppUserDto toAppUserDto(AppUser appUser);
}
