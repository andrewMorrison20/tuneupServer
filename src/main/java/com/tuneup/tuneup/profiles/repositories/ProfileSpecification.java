package com.tuneup.tuneup.profiles.repositories;

import com.tuneup.tuneup.genres.Genre;
import com.tuneup.tuneup.pricing.Price;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteria;
import com.tuneup.tuneup.regions.RegionRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import com.tuneup.tuneup.Instruments.Instrument;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.max;
import static java.util.Collections.min;


public class ProfileSpecification {

    public static Specification<Profile> bySearchCriteria(ProfileSearchCriteria criteria, RegionRepository regionRepository) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getProfileType() != null) {
                predicates.add(builder.equal(root.get("profileType"), criteria.getProfileType()));
            }


            if (criteria.getRating() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("averageRating"), criteria.getRating()));
            }


            if (criteria.getGenre() != null) {
                Join<Profile, Genre> genreJoin = root.join("genres");
                predicates.add(genreJoin.get("id").in(criteria.getGenre()));
            }

            if(criteria.getPriceRange()!=null){
                Double lowerLimit = min(criteria.getPriceRange());
                Double upperLimit = max(criteria.getPriceRange());

                Join<Profile, Price> priceJoin = root.join("prices");
                predicates.add(builder.between(priceJoin.get("rate"), lowerLimit, upperLimit));
            }

            if (criteria.getInstruments() != null) {
                Join<Profile, Instrument> instrumentJoin = root.join("instruments");
                predicates.add(instrumentJoin.get("id").in(criteria.getInstruments()));
            }
            if (criteria.getRegionId() != null) {
                List<Long> regionIds = regionRepository.findRegionAndChildrenIds(criteria.getRegionId());
                predicates.add(root.get("tuitionRegion").get("id").in(regionIds));
            }
            if (criteria.getKeyword() != null) {
                String keywordPattern = "%" + criteria.getKeyword().toLowerCase() + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("displayName")), keywordPattern)

                ));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
