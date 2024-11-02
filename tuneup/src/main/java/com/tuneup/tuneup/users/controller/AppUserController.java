package com.tuneup.tuneup.users.controller;

import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.repository.UserRepository;
import com.tuneup.tuneup.users.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @GetMapping
    public ResponseEntity<List<AppUserDto>> getAllUsers(){
        List<AppUserDto> allUsers = appUserService.findAll();
        return ResponseEntity.ok(allUsers);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<AppUserDto> createUser(@RequestBody AppUserDto appUserDto){
      AppUserDto createdUser = appUserService.createUser(appUserDto);
      return ResponseEntity.ok().body(createdUser);
    }
}

