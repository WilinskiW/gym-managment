package com.task.gymmanagment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
}
