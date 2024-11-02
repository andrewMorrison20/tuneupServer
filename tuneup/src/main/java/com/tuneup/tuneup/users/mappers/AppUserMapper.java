package com.tuneup.tuneup.users.mappers;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.repository.AppUser;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface AppUserMapper {

    AppUser toAppUser(AppUserDto appUserDto);

    AppUserDto toAppUserDto(AppUser appUser);
}
