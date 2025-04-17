package com.tuneup.tuneup.profiles.repositories;

import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.genres.entities.Genre;
import com.tuneup.tuneup.pricing.entities.Price;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteriaDto;
import com.tuneup.tuneup.qualifications.entities.ProfileInstrumentQualification;
import com.tuneup.tuneup.regions.repositories.RegionRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import com.tuneup.tuneup.Instruments.entities.Instrument;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.max;
import static java.util.Collections.min;


public class ProfileSpecification {

    public static Specification<Profile> bySearchCriteria(ProfileSearchCriteriaDto criteria, RegionRepository regionRepository, AvailabilityRepository availabilityRepository) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.isNull(root.get("deletedAt")));

            if (criteria.getProfileType() != null) {
                predicates.add(builder.equal(root.get("profileType"), criteria.getProfileType()));
            }

            if (criteria.getLessonType() != null && !criteria.getLessonType().isEmpty()) {
                predicates.add(root.get("lessonType").in(criteria.getLessonType()));
            }

            if (criteria.getQualifications() != null && !criteria.getQualifications().isEmpty()) {
                // Join Profile with ProfileInstrumentQualification
                Join<Profile, ProfileInstrumentQualification> qualificationJoin = root.join("profileInstrumentQualifications");
                predicates.add(qualificationJoin.get("qualification").get("id").in(criteria.getQualifications()));
            }

            if (criteria.getRating() != null && criteria.getRating() > 0) {
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


            if (criteria.getStartTime() != null && criteria.getEndTime() != null) {
                Set<Long> availableProfileIds = availabilityRepository.findAvailableProfileIds(
                        criteria.getStartTime(), criteria.getEndTime()
                );

                if (!availableProfileIds.isEmpty()) {
                    predicates.add(root.get("id").in(availableProfileIds));
                } else {
                    return builder.disjunction();
                }
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
