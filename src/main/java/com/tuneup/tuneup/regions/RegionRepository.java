package com.tuneup.tuneup.regions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByNameContainingIgnoreCase(String query);

    Optional<Region> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT r.id FROM Region r WHERE r.id = :parentId OR r.parentRegion.id = :parentId")
    List<Long> findRegionAndChildrenIds(@Param("parentId") Long parentId);
}
