package com.task.gymmanagement.infrastructure.controller;

import com.task.gymmanagement.domain.GymManagementFacade;
import com.task.gymmanagement.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagement.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagement.domain.dto.response.GymDto;
import com.task.gymmanagement.domain.dto.response.MembershipPlanDto;
import com.task.gymmanagement.domain.dto.response.RevenueReportDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GymController {
    private final GymManagementFacade managementFacade;

    @PostMapping("/gyms")
    public ResponseEntity<Long> addGym(@Valid @RequestBody AddGymRequestDto gymRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(managementFacade.addGym(gymRequestDto));
    }

    @GetMapping("/gyms")
    public ResponseEntity<List<GymDto>> getAllGyms() {
        return ResponseEntity.ok(managementFacade.getAllGyms());
    }

    @PostMapping("/membership-plans")
    public ResponseEntity<Long> createMembershipPlanForGivenGym(@Valid @RequestBody
                                                                AddMembershipPlanRequestDto membershipPlanRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(managementFacade.addMembershipToGym(membershipPlanRequest));
    }

    @GetMapping("/gyms/{id}/membership-plans")
    public ResponseEntity<List<MembershipPlanDto>> getAllMembershipPlansForGym(@PathVariable Long id) {
        return ResponseEntity.ok(managementFacade.getGymAllMembershipPlans(id));
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<List<RevenueReportDto>> getRevenueReport() {
        return ResponseEntity.ok(managementFacade.getRevenueReport());
    }
}
