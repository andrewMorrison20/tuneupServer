package com.tuneup.tuneup.users.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String token;
    private AppUserDto userDto;

    public LoginResponseDto(String token) {
        this.token = token;
    }
}
