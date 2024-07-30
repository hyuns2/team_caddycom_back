package com.flash21.caddycom.service.assignment;

import com.flash21.caddycom.dto.PagingResponse;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import com.flash21.caddycom.repository.reservationSheet.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AssignmentCaddyService {
    private final AssignmentRepository assignmentRepository;

    /**
     * 골프장 id와 date로 assignment를 모두 조회한다.
     *
     */
    @Transactional(readOnly = true)
    public PagingResponse<AssignmentResponse.Info> getAssignments(Long golfFieldId, LocalDate date, Long courseId, AssignmentStatus status, int page) {
        return null;
    }
}
