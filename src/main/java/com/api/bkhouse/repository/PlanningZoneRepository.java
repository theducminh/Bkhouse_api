package com.api.bkhouse.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.api.bkhouse.entity.PlanningZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface PlanningZoneRepository extends JpaRepository<PlanningZone, Long> {
    
    // Ép chết thứ tự bằng ?1 và ?2:
    // ?1 sẽ là tham số thứ nhất truyền vào hàm (lat)
    // ?2 sẽ là tham số thứ hai truyền vào hàm (lng)
    // Trong ST_Point: Kinh độ (lng - ?2) bắt buộc phải đứng TRƯỚC, Vĩ độ (lat - ?1) đứng SAU
    @Query(value = "SELECT zone_name, zone_type FROM planning_zones " +
                   "WHERE ST_Within(" +
                   "  ST_SetSRID(ST_Point(?2, ?1), 4326), " +
                   "  geom" +
                   ") LIMIT 1", 
           nativeQuery = true)
    List<Object[]> checkPointInPlanning(double lat, double lng);
}