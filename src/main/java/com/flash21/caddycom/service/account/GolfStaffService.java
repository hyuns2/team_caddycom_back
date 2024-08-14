package com.flash21.caddycom.service.account;

import com.flash21.caddycom.dto.golfStaff.GolfStaffRequest;
import com.flash21.caddycom.dto.golfStaff.GolfStaffResponse;
import com.flash21.caddycom.entity.account.GolfStaff;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.account.GolfStaffRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GolfStaffService {
    private final GolfStaffRepository golfStaffRepository;
    private final GolfFieldRepository golfFieldRepository;

    public List<GolfStaffResponse.Info> getGolfStaff(Long id) {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("골프장이 존재하지 않습니다."));

        List<GolfStaff> golfStaffs = golfStaffRepository.findAllByGolfField(golfField);
        return golfStaffs.stream()
                .map(GolfStaffResponse.Info::from)
                .toList();
    }

    @Transactional
    public void createGolfStaff(Long id, List<GolfStaffRequest.Create> requestList) {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("골프장이 존재하지 않습니다."));

        List<GolfStaff> golfStaffs = requestList.stream()
                .map(GolfStaffRequest.Create::toEntity)
                .peek(account -> account.linkGolfField(golfField))
                .toList();
        golfStaffRepository.saveAll(golfStaffs);
    }
}
