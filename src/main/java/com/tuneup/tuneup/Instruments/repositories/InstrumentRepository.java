package com.tuneup.tuneup.Instruments.repositories;

import com.tuneup.tuneup.Instruments.entities.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    boolean existsByName(String name);
}
