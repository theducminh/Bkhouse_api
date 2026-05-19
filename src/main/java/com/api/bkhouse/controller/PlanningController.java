package com.api.bkhouse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.bkhouse.payload.request.CheckPlanningRequest;
import com.api.bkhouse.payload.response.PlanningResponse;
import com.api.bkhouse.service.PlanningService;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/check-planning")
@CrossOrigin(origins = "*")
public class PlanningController {
    private final PlanningService planningService;

    public PlanningController(PlanningService planningService) {
        this.planningService = planningService;
    }

    @PostMapping
    public ResponseEntity<PlanningResponse> checkPlanning(@RequestBody CheckPlanningRequest request) {
        PlanningResponse response = planningService.checkLocation(request);
        return ResponseEntity.ok(response);
    }
}
