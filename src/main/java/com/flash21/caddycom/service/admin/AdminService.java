package com.flash21.caddycom.service.admin;

import com.flash21.caddycom.entity.golfField.ApprovalStatus;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
