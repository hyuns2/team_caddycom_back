package com.flash21.caddycom.repository;

import com.flash21.caddycom.entity.ReservationDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationDateRepository extends JpaRepository<ReservationDate, Long> {
}
