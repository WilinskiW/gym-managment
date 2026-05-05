package com.task.gymmanagement.infrastructure.controller;

import com.task.gymmanagement.domain.GymManagementFacade;
import com.task.gymmanagement.domain.dto.request.AddMemberRequestDto;
import com.task.gymmanagement.domain.dto.response.MemberDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final GymManagementFacade managementFacade;

    @PostMapping
    public ResponseEntity<MemberDto> addMemberToMembershipPlan(@Valid @RequestBody AddMemberRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(managementFacade.registerMember(dto));
    }

    @GetMapping
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        return ResponseEntity.ok(managementFacade.getAllMembers());
    }

    @PatchMapping("/{memberId}/cancel")
    public ResponseEntity<Void> cancelMembership(@PathVariable Long memberId) {
        managementFacade.cancelMembership(memberId);
        return ResponseEntity.noContent().build();
    }
}
