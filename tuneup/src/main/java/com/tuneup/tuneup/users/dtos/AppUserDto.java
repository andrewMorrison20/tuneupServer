package com.tuneup.tuneup.users.dtos;

import org.springframework.stereotype.Component;

@Component
public class AppUserDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String username;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }
    public void setUsername() {
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }

    public void setPassword() {
        this.password = password;
    }
}
