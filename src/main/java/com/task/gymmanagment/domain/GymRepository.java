package com.task.gymmanagment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface GymRepository extends JpaRepository<Gym, Long> {
    boolean existsByName(String name);
}
