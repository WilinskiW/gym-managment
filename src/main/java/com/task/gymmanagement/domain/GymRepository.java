package com.task.gymmanagement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface GymRepository extends JpaRepository<Gym, Long> {
    boolean existsByName(String name);
}
