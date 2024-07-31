package com.flash21.caddycom.service.assignment;

import com.flash21.caddycom.dto.PagingResponse;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import com.flash21.caddycom.repository.reservationSheet.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AssignmentCaddyService {

    private final AssignmentRepository assignmentRepository;
    private final HouseCaddyRepository houseCaddyRepository;

    /**
     * 골프장 id와 date로 assignment를 모두 조회한다.
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


    @Transactional(readOnly = true)
    public PagingResponse<AssignmentResponse.Info> getSwitchingCaddy(Long golfFieldId, LocalDate date, Long id, Long courseId, Integer part, int page) {
        Pageable pageable = PageRequest.of(page, 30);
        Page<Assignment> assignmentPage;

        if (courseId != null && part != null){
            assignmentPage = assignmentRepository.findAssignedByDateAndCourseIdAndPart(pageable,id,golfFieldId,date,courseId,part);
        } else if (courseId != null) {
            assignmentPage = assignmentRepository.findAssignedByDateAndCourseId(pageable,id,golfFieldId,date,courseId);
        } else if (part != null) {
            assignmentPage = assignmentRepository.findAssignedByDateAndPart(pageable,id,golfFieldId,date,part);
        } else {
            assignmentPage = assignmentRepository.findAssignedByDate(pageable,id,golfFieldId,date);
        }
        return PagingResponse.from(assignmentPage, AssignmentResponse.Info::fromSwitchable);
    }




    // TODO: CANCELED, ASSIGNED 상태일때만 조회 가능하도록 예외처리 추가 필요
    @Transactional(readOnly = true)
    public AssignmentResponse.Detail getAssignmentDetail(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));
        return AssignmentResponse.Detail.from(assignment);
    }



    @Transactional
    public void cancelAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));
        assignment.cancel();
    }


    @Transactional
    public void switchAssignment(Long from, Long to) {
        List<Assignment> assignments = assignmentRepository.findByIds(List.of(from, to));
        if (assignments.size() != 2)
            throw new IllegalArgumentException("해당 배정 정보가 없습니다.");
        // swap
        swapCaddy(assignments.get(0), assignments.get(1));
    }


    /**
     * sql batch 처리로 인해 one-to-one 관계의 엔티티를 변경 시 duplicate key 에러가 발생
     * -> 두 엔티티의 caddy를 null로 변경 후 다시 업데이트
     */
    private void swapCaddy(Assignment fromAssignment, Assignment toAssignment) {
        HouseCaddy fromCaddy = fromAssignment.getCaddy();
        String fromCaddyName = fromAssignment.getCaddyName();
        HouseCaddy toCaddy = toAssignment.getCaddy();
        String toCaddyName = toAssignment.getCaddyName();

        fromAssignment.vacateCaddy();
        toAssignment.vacateCaddy();

        assignmentRepository.switchAssignment(fromAssignment.getId(), toCaddy, toCaddyName);
        assignmentRepository.switchAssignment(toAssignment.getId(), fromCaddy, fromCaddyName);
    }


    @Transactional
    public void assignSelectedCaddy(Long assignmentId, Long caddyId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));
        HouseCaddy caddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 캐디입니다."));

        assignment.assignCaddy(caddy);
    }
}
