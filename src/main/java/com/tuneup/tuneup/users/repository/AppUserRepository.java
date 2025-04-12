package com.tuneup.tuneup.users.repository;

import com.tuneup.tuneup.users.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE AppUser u SET u.deletedAt = :timestamp WHERE u.id IN :ids")
    void softDeleteUsersByIds(@Param("ids") List<Long> ids, @Param("timestamp") LocalDateTime timestamp);

    AppUser findByEmail(String email);
}
