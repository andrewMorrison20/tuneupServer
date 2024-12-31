    package com.tuneup.tuneup.pricing;

    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.Optional;
    import java.util.Set;

    @Repository
    public interface PriceRepository extends JpaRepository<Price, Long> {

        Set<Price> findByStandardPricingTrue();

        Optional<Price> findByRateAndDescription(Double rate, String description);

        Set<Price> findByStandardPricingFalse();

        Optional<Price> findByPeriodAndRate(Period period, Double rate);
    }