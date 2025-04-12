package com.tuneup.tuneup.users.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tuneup.tuneup.address.AddressDto;
import com.tuneup.tuneup.roles.services.Role;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AppUserDto {

    private Long id;
    private String name;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String username;
    private AddressDto address;
    private Boolean isVerified;
    private Set<Role> roles;

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public Long getId() {

        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public AddressDto getAddress() {
        return this.address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
