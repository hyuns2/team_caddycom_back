package com.flash21.caddycom.service.admin;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.golfField.ApprovalStatus;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.global.common.fileReader.EntityConverter;
import com.flash21.caddycom.global.common.fileReader.ExcelReader;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.service.caddy.HouseCaddyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 전체 시스템 관리자가 골프장의 등록 요청을 승인, 거절
 *
 * @see GolfFieldRepository : 골프장 조회, 상태 변경을 위한 repository
 * @author kwonssshyeon
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final GolfFieldRepository golfFieldRepository;
    private final HouseCaddyService houseCaddyService;
    private final ExcelReader excelReader;
    private final EntityConverter entityConverter;

    /**
     * 전체 시스템 관리자가 골프장 등록을 승인
     * @param id 골프장 id, null이 들어갈 수 없다.
     */
    @Transactional
    public void approveRegistration(Long id){
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));
        if (golfField.getStatus()!= ApprovalStatus.WAITING){
            throw new IllegalStateException("이미 처리된 골프장입니다.");
        }

        golfField.approve();
    }


    /**
     * 전체 시스템 관리자가 골프장 등록을 거절
     * @param id 골프장 id, null이 들어갈 수 없다.
     */
    @Transactional
    public void rejectRegistration(Long id){
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));
        if (golfField.getStatus()!= ApprovalStatus.WAITING){
            throw new IllegalStateException("이미 처리된 골프장입니다.");
        }

        golfField.reject();
    }

    @Transactional
    public void uploadCaddy(Long id, MultipartFile file) {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));

        List<List<String>> stringData = excelReader.readExcelToList(file);
        List<HouseCaddy> caddyList = stringData.stream()
                .map(entityConverter::toEntity)
                .toList();

        houseCaddyService.saveCaddyList(golfField.getId(), caddyList);
    }
}
