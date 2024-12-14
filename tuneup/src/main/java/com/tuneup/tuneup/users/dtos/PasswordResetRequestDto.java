package com.tuneup.tuneup.users.dtos;

import org.springframework.stereotype.Component;

@Component
public class PasswordResetRequestDto {

    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
