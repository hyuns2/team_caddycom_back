package com.flash21.caddycom.repository;

import com.flash21.caddycom.entity.reservationSheet.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}
