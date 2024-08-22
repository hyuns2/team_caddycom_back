package com.flash21.caddycom.service.golfStaff;

import com.flash21.caddycom.dto.golfStaff.GolfStaffCommand;
import com.flash21.caddycom.dto.golfStaff.GolfStaffResponse;
import com.flash21.caddycom.entity.account.GolfStaff;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.golfStaff.GolfStaffRepository;
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

    /**
     * 골프장에 속한 직원들을 조회
     */
    public List<GolfStaffResponse.Info> getGolfStaff(Long id) {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("골프장이 존재하지 않습니다."));

        List<GolfStaff> golfStaffs = golfStaffRepository.findAllByGolfField(golfField);
        return golfStaffs.stream()
                .map(GolfStaffResponse.Info::from)
                .toList();
    }

    /**
     * 골프장 직원 List로 생성
     */
    @Transactional
    public void createGolfStaff(Long id, List<GolfStaffCommand.Create> requestList) {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("골프장이 존재하지 않습니다."));

        List<GolfStaff> golfStaffs = requestList.stream()
                .map(GolfStaffCommand.Create::toEntity)
                .peek(staff -> staff.linkGolfField(golfField))
                .toList();
        golfStaffRepository.saveAll(golfStaffs);
    }
}
