package com.tuneup.tuneup.users.repository;

import com.tuneup.tuneup.users.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    AppUser findByUsername(String username);

    AppUser findByEmail(String email);
}
