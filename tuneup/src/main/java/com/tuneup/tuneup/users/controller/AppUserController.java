package com.tuneup.tuneup.users.controller;

import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private AppUserMapper appUserMapper;

    @GetMapping("/findall")
    public ResponseEntity<List<AppUserDto>> getAllUsers(){
        List<AppUserDto> allUsers = appUserService.findAll();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping()
    public ResponseEntity<AppUserDto> getUserByUsername(@RequestParam("email") String email){
        AppUserDto user = appUserService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<AppUserDto> getUserDetails(@RequestParam("email") String email){
        AppUserDto user = appUserService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> getUserDetails(@PathVariable("id") long id){
        AppUser user = appUserService.findById(id);
        appUserMapper.toAppUserDto(user);
        return ResponseEntity.ok(appUserMapper.toAppUserDto(user));
    }

    @PutMapping("/update")
    public ResponseEntity<AppUserDto> updateUser(@RequestBody AppUserDto appUserDto){
        AppUserDto updatedAppUserDto = appUserService.updateUser(appUserDto);
        return ResponseEntity.ok(updatedAppUserDto);
    }

    @PostMapping("/createNew")
    public ResponseEntity<AppUserDto> createUser(@RequestBody AppUserDto appUserDto){
      AppUserDto createdUser = appUserService.createUser(appUserDto);
      return ResponseEntity.ok().body(createdUser);
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<AppUserDto> resetPassword(@RequestBody AppUserDto appUserDto){
        return null;
    }
}

