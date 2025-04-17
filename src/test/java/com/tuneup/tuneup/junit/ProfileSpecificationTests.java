package com.tuneup.tuneup.junit;

import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteriaDto;
import com.tuneup.tuneup.profiles.repositories.ProfileSpecification;
import com.tuneup.tuneup.regions.repositories.RegionRepository;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileSpecificationTests {

    @Mock RegionRepository regionRepo;
    @Mock AvailabilityRepository availRepo;

    @Mock
    Root<Profile> root;
    @Mock
    CriteriaQuery<?> query;
    @Mock
    CriteriaBuilder builder;

    @Mock
    Path<Object> deletedAtPath;
    @Mock Predicate deletedAtPredicate;

    @BeforeEach
    void setUp() {
        // stub the always-present deletedAt check
        when(root.get("deletedAt")).thenReturn(deletedAtPath);
        when(builder.isNull(deletedAtPath)).thenReturn(deletedAtPredicate);
    }

    @Test
    void bySearchCriteria_emptyCriteria_onlyDeletedAt() {
        ProfileSearchCriteriaDto crit = new ProfileSearchCriteriaDto();

        // final AND(...) -> return a dummy predicate
        Predicate finalPred = mock(Predicate.class);
        when(builder.and(new Predicate[]{deletedAtPredicate})).thenReturn(finalPred);

        Predicate result = ProfileSpecification
                .bySearchCriteria(crit, regionRepo, availRepo)
                .toPredicate(root, query, builder);

        assertSame(finalPred, result);
        verify(builder).isNull(deletedAtPath);
        verify(builder).and(new Predicate[]{deletedAtPredicate});
        verifyNoMoreInteractions(regionRepo, availRepo);
    }

    @Test
    void bySearchCriteria_withRegionAndAvailability_emptyAvail_disjunction() {
        ProfileSearchCriteriaDto crit = new ProfileSearchCriteriaDto();
        crit.setRegionId(42L);
        crit.setStartTime(LocalDateTime.now());
        crit.setEndTime(LocalDateTime.now().plusHours(2));


        when(root.get("deletedAt")).thenReturn(deletedAtPath);
        when(builder.isNull(deletedAtPath)).thenReturn(deletedAtPredicate);
        when(regionRepo.findRegionAndChildrenIds(42L)).thenReturn(List.of(7L, 8L));
        when(availRepo.findAvailableProfileIds(crit.getStartTime(), crit.getEndTime()))
                .thenReturn(Set.of());

        Path<Object> tuitionRegionPath = mock(Path.class);
        Path<Object> tuitionRegionIdPath = mock(Path.class);
        when(root.get("tuitionRegion")).thenReturn(tuitionRegionPath);
        when(tuitionRegionPath.get("id")).thenReturn(tuitionRegionIdPath);
        Predicate regionInPredicate = mock(Predicate.class);
        when(tuitionRegionIdPath.in(List.of(7L, 8L))).thenReturn(regionInPredicate);

        Predicate disj = mock(Predicate.class);
        when(builder.disjunction()).thenReturn(disj);

        // 6) invoke
        Predicate result = ProfileSpecification
                .bySearchCriteria(crit, regionRepo, availRepo)
                .toPredicate(root, query, builder);

        // 7) verify
        assertSame(disj, result);
        verify(builder).isNull(deletedAtPath);
        verify(regionRepo).findRegionAndChildrenIds(42L);
        verify(availRepo).findAvailableProfileIds(crit.getStartTime(), crit.getEndTime());
        verify(builder).disjunction();
    }

}
