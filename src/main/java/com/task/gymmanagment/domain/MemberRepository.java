package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.response.RevenueReportDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface MemberRepository extends JpaRepository<Member, Long> {
    long countMembersByMembershipPlan(MembershipPlan membershipPlan);

    List<RevenueReportDto> calculateRevenueReport();
}
