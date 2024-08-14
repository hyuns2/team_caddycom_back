package com.flash21.caddycom.repository.account;

import com.flash21.caddycom.entity.account.GolfStaff;
import com.flash21.caddycom.entity.golfField.GolfField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GolfStaffRepository extends JpaRepository<GolfStaff, Long> {
    Optional<GolfStaff> findByPhoneNumber(String phoneNumber);
    List<GolfStaff> findAllByGolfField(GolfField golfField);
}
