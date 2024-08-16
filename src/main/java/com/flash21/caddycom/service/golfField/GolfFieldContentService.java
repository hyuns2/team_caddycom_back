package com.flash21.caddycom.service.golfField;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.golfStaff.GolfStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GolfFieldContentService {
    private final GolfFieldRepository golfFieldRepository;
    private final GolfStaffRepository golfStaffRepository;

    @Transactional
    protected void saveFieldAndSetStaff(GolfField golfField, String phoneNumber) {
        golfFieldRepository.save(golfField);
        golfStaffRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(staff -> staff.linkGolfField(golfField));
    }
}
