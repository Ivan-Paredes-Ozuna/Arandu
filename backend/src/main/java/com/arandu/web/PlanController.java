package com.arandu.web;

import com.arandu.dto.PlanDtos;
import com.arandu.security.UserPrincipal;
import com.arandu.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/availability")
    public PlanDtos.AvailabilityResponse getAvailability(@AuthenticationPrincipal UserPrincipal principal) {
        return planService.getAvailability(principal.getId());
    }

    @PutMapping("/availability")
    public PlanDtos.AvailabilityResponse saveAvailability(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PlanDtos.AvailabilityRequest request) {
        return planService.saveAvailability(principal.getId(), request);
    }

    @PostMapping("/plans/generate")
    public PlanDtos.PlanResponse generate(@AuthenticationPrincipal UserPrincipal principal) {
        return planService.generatePlan(principal.getId());
    }

    @GetMapping("/plans/current")
    public PlanDtos.PlanResponse current(@AuthenticationPrincipal UserPrincipal principal) {
        return planService.currentPlan(principal.getId());
    }
}
