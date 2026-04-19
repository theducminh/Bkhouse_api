package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.bkhouse.entity.Interested;
import com.api.bkhouse.entity.response.IPostInterested;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterestedRepository extends JpaRepository<Interested, Long> {
    @Query(value = "select rep.id, rep.type, rep.title, rep.price, rep.area, rep.is_sell as isSell, rep.address_show as addressShow\n" +
            "from real_estate_post rep inner join interested i \n" +
            "on i.real_estate_post_id = rep.id\n" +
            "where rep.enable = 1\n" +
            "and rep.status = 'DA_KIEM_DUYET'\n" +
            "and i.user_id = :userId", nativeQuery = true)
    List<IPostInterested> findRepDetailByUserId(UUID userId);
    @Query(value = "select rep.id, rep.type, rep.title, rep.price, rep.area, rep.is_sell as isSell, rep.address_show as addressShow\n" +
            "from real_estate_post rep inner join interested i \n" +
            "on i.real_estate_post_id = rep.id\n" +
            "where rep.enable = 1\n" +
            "and rep.status = 'DA_KIEM_DUYET'\n" +
            "and i.user_id = 'anonymous'\n" +
            "and i.device_info = :deviceInfo", nativeQuery = true)
    List<IPostInterested> findRepDetailByDeviceInfo(String deviceInfo);
    boolean existsByDeviceInfoAndRealEstatePostId(String deviceInfo, UUID realEstatePostId);
    boolean existsByUserIdAndRealEstatePostId(UUID userId, UUID realEstatePostId);
    Optional<Interested> findByDeviceInfoAndRealEstatePostId(String deviceInfo, UUID realEstatePostId);
    Optional<Interested> findByUserIdAndRealEstatePostId(UUID userId, UUID realEstatePostId);
    Integer countByUserIdAndDeviceInfo(UUID userId, String deviceInfo);
    Integer countByUserId(UUID userId);
    long countByRealEstatePostId(UUID realEstatePostId);
}
