package com.flash21.caddycom.service.assignment;

import com.flash21.caddycom.dto.PagingResponse;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import com.flash21.caddycom.repository.reservationSheet.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Pageable pageable = PageRequest.of(page, 30);
        Page<Assignment> assignmentPage;
        if (courseId != null && status != null){
            assignmentPage = assignmentRepository.findAllByDateAndCourseIdAndStatus(pageable,golfFieldId, date, courseId, status);
        } else if (courseId != null) {
            assignmentPage = assignmentRepository.findAllByDateAndCourseId(pageable,golfFieldId,date,courseId);
        } else if (status != null) {
            assignmentPage = assignmentRepository.findAllByDateAndStatus(pageable,golfFieldId,date,status);
        } else {
            assignmentPage = assignmentRepository.findAllByDate(pageable, golfFieldId, date);
        }
        return PagingResponse.from(assignmentPage, AssignmentResponse.Info::from);
    }
}
