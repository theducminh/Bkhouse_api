package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.payload.request.CheckPlanningRequest;
import com.api.bkhouse.payload.response.PlanningResponse;
import com.api.bkhouse.repository.PlanningZoneRepository;
import java.util.List;

@Service
public class PlanningService {
    @Autowired
    private PlanningZoneRepository planningZoneRepository;

    public PlanningResponse checkLocation(CheckPlanningRequest request) {
        List<Object[]> result = planningZoneRepository.checkPointInPlanning(request.getLat(), request.getLng());
        
        if (!result.isEmpty()) {
            Object[] row = result.get(0);
            String name = (String) row[0];
            String type = (String) row[1];
            return new PlanningResponse(true, name, type);
        }
        return new PlanningResponse(false, "Đất an toàn", "Không có quy hoạch");
    }
}
