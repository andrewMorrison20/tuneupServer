package com.tuneup.tuneup.profiles.repositories;



import aj.org.objectweb.asm.commons.Remapper;
import com.tuneup.tuneup.profiles.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Profile findByAppUserId(Long userId);

    boolean existsByAppUserId(Long userId);
}

