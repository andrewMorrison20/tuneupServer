package com.tuneup.tuneup.address.repositories;

import com.tuneup.tuneup.address.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a " +
            "JOIN AppUser u ON u.address.id = a.id " +
            "JOIN Profile p ON p.appUser.id = u.id " +
            "WHERE p.id = :profileId")
    Optional<Address> findByProfileId(@Param("profileId") Long profileId);




    @Query("SELECT a FROM Address a WHERE a.addressLine1 = :addressLine1 AND a.postcode = :postcode")
    Address findAddressIdByAddressLine1AndPostcode(@Param("addressLine1") String addressLine1,
                                                   @Param("postcode") String postcode);

    Set<Address> findAllByPostcode(String postcode);

}
