package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.bkhouse.entity.Interested;
import com.api.bkhouse.entity.response.IPostInterested;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterestedRepository extends JpaRepository<Interested, Long> {
   @Query(value = "select cast(rep.id as varchar) as id, rep.type, rep.title, rep.price, rep.area, rep.is_sell as isSell, rep.address_show as addressShow " +
            "from real_estate_posts rep inner join interested i " +
            "on i.real_estate_post_id = rep.id " +
            "where rep.enable = true " +
            "and rep.status = 'APPROVED' " +
            "and i.user_id = CAST(:userId AS uuid)", nativeQuery = true)
    List<IPostInterested> findRepDetailByUserId(@Param("userId") UUID userId);

    @Query(value = "select cast(rep.id as varchar) as id, rep.type, rep.title, rep.price, rep.area, rep.is_sell as isSell, rep.address_show as addressShow\n" +
        "from real_estate_posts rep inner join interested i \n" +
        "on i.real_estate_post_id = rep.id\n" +
        "where rep.enable = true\n" +
        "and rep.status = 'APPROVED'\n" +
        "and i.user_id = '00000000-0000-0000-0000-000000000000'::uuid\n" + // ✨ THÊM ::uuid VÀO ĐÂY BÁC NHÉ
        "and i.device_info = :deviceInfo", nativeQuery = true)
List<IPostInterested> findRepDetailByDeviceInfo(@Param("deviceInfo") String deviceInfo);
    boolean existsByDeviceInfoAndRealEstatePostId(String deviceInfo, UUID realEstatePostId);
    boolean existsByUserIdAndRealEstatePostId(UUID userId, UUID realEstatePostId);
    Optional<Interested> findByDeviceInfoAndRealEstatePostId(String deviceInfo, UUID realEstatePostId);
    Optional<Interested> findByUserIdAndRealEstatePostId(UUID userId, UUID realEstatePostId);
    Integer countByUserIdAndDeviceInfo(UUID userId, String deviceInfo);
    Integer countByUserId(UUID userId);
    long countByRealEstatePostId(UUID realEstatePostId);
}
