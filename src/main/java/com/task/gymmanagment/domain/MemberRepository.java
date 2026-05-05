package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.response.RevenueReportDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT COUNT(m) FROM Member m WHERE m.membershipPlan = ?1 AND m.status = 'ACTIVE'")
    long countActiveMembersByMembershipPlan(MembershipPlan membershipPlan);

    @Query("""
                SELECT p.gym.name AS gymName,
                       SUM(p.amount) AS totalRevenue,
                       p.currency AS currencyCode
                FROM Member m
                JOIN m.membershipPlan p
                WHERE m.status = 'ACTIVE'
                GROUP BY p.gym.name, p.currency
            """)
    List<RevenueReportDto> calculateRevenueReport();
}
