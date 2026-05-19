package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.RealEstatePostAgency;

import java.util.List;
import java.util.UUID;

@Repository
public interface RealEstatePostAgencyRepository extends JpaRepository<RealEstatePostAgency, Long> {
    List<RealEstatePostAgency> findByAgencyId(UUID agencyId);
    List<RealEstatePostAgency> findByCreateBy(UUID createBy);

    @Query(value = "SELECT COUNT(*) " +
                   "FROM real_estate_posts rep " +
                   "INNER JOIN real_estate_post_agency repa ON rep.id = repa.real_estate_post_id " +
                   "INNER JOIN agency_district ad ON rep.district_code = ad.district_code " +
                   "WHERE rep.is_enabled = true " +
                   "AND rep.id = :repId " +
                   "AND ad.user_id = :agencyId", 
           nativeQuery = true)
    Long checkInArea(@Param("repId") UUID repId, @Param("agencyId") UUID agencyId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE real_estate_posts SET priority = 4, period = 365 WHERE id = :repId", nativeQuery = true)
    void updateRep(@Param("repId") UUID repId);
}
