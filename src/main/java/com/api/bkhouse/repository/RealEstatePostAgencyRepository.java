package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.RealEstatePostAgency;

import java.util.List;
import java.util.UUID;

@Repository
public interface RealEstatePostAgencyRepository extends JpaRepository<RealEstatePostAgency, Long> {
    List<RealEstatePostAgency> findByAgencyId(UUID agencyId);
    List<RealEstatePostAgency> findByCreateBy(UUID createBy);

    @Query(value = "select count(*)\n" +
            "from real_estate_post rep, agency_district ad, real_estate_post_agency repa\n" +
            "where rep.id = repa.real_estate_post_id\n" +
            "and rep.district_code = ad.district_code\n" +
            "and repa.agency_id = ad.user_id\n" +
            "and rep.enable = 1\n" +
            "and rep.id = :repId\n" +
            "and ad.user_id = :agencyId", nativeQuery = true)
    Integer checkInArea(@Param("repId") UUID repId, @Param("agencyId") UUID agencyId);

    @Modifying
    @Query(value = "update real_estate_post set priority = 4, period = 365 where id = :repId", nativeQuery = true)
    void updateRep(@Param("repId") UUID repId);
}
