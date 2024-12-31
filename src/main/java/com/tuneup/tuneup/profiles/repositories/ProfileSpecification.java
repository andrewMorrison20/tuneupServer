package com.tuneup.tuneup.profiles.repositories;

import com.tuneup.tuneup.genres.Genre;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import com.tuneup.tuneup.Instruments.Instrument;
import java.util.ArrayList;
import java.util.List;


public class ProfileSpecification {

    public static Specification<Profile> bySearchCriteria(ProfileSearchCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getProfileType() != null) {
                predicates.add(builder.equal(root.get("profileType"), criteria.getProfileType()));
            }

            if (criteria.getGenre() != null) {
                Join<Profile, Genre> genreJoin = root.join("genres");
                predicates.add(genreJoin.get("id").in(criteria.getGenre()));
            }

            if (criteria.getInstruments() != null) {
                Join<Profile, Instrument> instrumentJoin = root.join("instruments");
                predicates.add(instrumentJoin.get("id").in(criteria.getInstruments()));
            }

            if (criteria.getKeyword() != null) {
                String keywordPattern = "%" + criteria.getKeyword().toLowerCase() + "%";
                predicates.add(builder.or(
                        //builder.like(builder.lower(root.get("tuitionRegion")), keywordPattern),
                        builder.like(builder.lower(root.get("displayName")), keywordPattern)
                ));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
