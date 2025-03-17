package com.flash21.caddycom.service.assignment;

import com.flash21.caddycom.dto.assignment.AssignmentRequest;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.repository.assignment.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {
    final AssignmentRepository assignmentRepository;

    /**
     * 해당 배정을 BLOCK 상태로 만들고, 블락된 사유를 저장한다.
     */
    @Transactional
    public void setBlock(Long assignmentsId, AssignmentRequest.Block blockRequest) {
        Assignment findAssignment = assignmentRepository.findById(assignmentsId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        findAssignment.blockAssignment(blockRequest.getReason());

    }

    /**
     * 해당 배정을 CANCEL 상태로 만든다.
     * (캐디의 취소요청에 대한 승인이거나 골프장 관리자가 직접 취소한 상황일 수 있다.)
     */
    @Transactional
    public void cancelBlock(Long assignmentsId) {
        Assignment findAssignment = assignmentRepository.findById(assignmentsId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        findAssignment.cancelBlock();

    }

    public AssignmentResponse.Block getBlock(Long assignmentsId) {
        Assignment findAssignment = assignmentRepository.findById(assignmentsId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        if (findAssignment.getAssignmentStatus() != AssignmentStatus.BLOCKED) {
            throw new IllegalStateException("블락상태가 아닌 배정 정보입니다.");
        }

        return new AssignmentResponse.Block(findAssignment);
    }


    /**
     * 프리캐디에 배정 요청: 배정상태가 ASSIGN_REQUESTED로 변경되어 프리캐디측에서 조회할 수 있게 된다.
     */
    @Transactional
    public void requestFreeCaddy(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));
        if (assignment.getAssignmentStatus() != AssignmentStatus.ASSIGNED)
            throw new IllegalArgumentException("이미 배정된 요청입니다.");

        assignment.requestFreeCaddy();
        // TODO: 구독 중인 외부캐디에 알림 보내기 추가
    }
}