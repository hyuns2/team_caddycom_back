package com.flash21.caddycom.service.assignment;

import com.flash21.caddycom.dto.PagingResponse;
import com.flash21.caddycom.dto.assignment.AssignmentRequest;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.entity.caddy.Caddy;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.assignment.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static com.flash21.caddycom.entity.schedule.AssignmentStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentCaddyService {
    private final AssignmentRepository assignmentRepository;
    private final HouseCaddyRepository houseCaddyRepository;
    private final CourseRepository courseRepository;

    /**
     * 골프장 id와 date로 assignment를 페이징 조회한다.
     */
    public PagingResponse<AssignmentResponse.Info> getAssignments(Long golfFieldId, LocalDate date, Long courseId, AssignmentStatus status, int page) {
        Pageable pageable = PageRequest.of(page, 30);
        Page<Assignment> assignmentPage =
                assignmentRepository.findAllByDateAndCourseIdAndStatus(pageable, golfFieldId, date, courseId, status);
        return PagingResponse.from(assignmentPage, AssignmentResponse.Info::from);
    }

    /**
     * 변경할 하우스 캐디의 목록을 페이징 조회한다.
     */
    public PagingResponse<AssignmentResponse.Info> getSwitchingCaddy(Long golfFieldId, LocalDate date, Long id, Long courseId, Integer part, int page) {
        Pageable pageable = PageRequest.of(page, 30);
        Page<Assignment> assignmentPage =
                assignmentRepository.findAssignedByDateAndCourseIdAndPart(pageable, id, golfFieldId, date, courseId, part);
        return PagingResponse.from(assignmentPage, AssignmentResponse.Info::fromSwitchable);
    }

    /**
     * 배정 상세 조회
     */
    public AssignmentResponse.Detail getAssignmentDetail(Long assignmentId) {
        Assignment assignment = assignmentRepository.findByIdWithFetchJoin(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));

        if (assignment.getAssignmentStatus() != CANCELED && assignment.getAssignmentStatus() != ASSIGNED) {
            throw new IllegalArgumentException("배정되거나 취소된 상태에서만 조회 가능합니다.");
        }
        return AssignmentResponse.Detail.from(assignment);
    }


    /**
     * 배정 취소(또는 취소요청을 승인)
     */
    @Transactional
    public void cancelAssignment(Long assignmentId, String reason) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));
        assignment.cancel(reason);
    }

    /**
     * 하우스캐디의 배정 취소 요청
     */
    @Transactional
    public void requestCancelAssignment(Long assignmentId, AssignmentRequest.Cancel request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));
        assignment.requestCancel(request.getReason());
    }

    /**
     * 두 캐디간 배정 변경
     */
    @Transactional
    public void switchAssignment(Long from, Long to) {
        List<Assignment> assignments = assignmentRepository.findByIds(List.of(from, to));
        if (assignments.size() != 2)
            throw new IllegalArgumentException("해당 배정 정보가 없습니다.");

        if (assignments.stream().anyMatch(assignment -> assignment.getAssignmentStatus() != ASSIGNED))
            throw new IllegalArgumentException("이미 취소되거나 블락된 배정입니다. 변경이 불가능합니다.");
        // swap
        Caddy fromCaddy = assignments.get(0).getCaddy();
        Caddy toCaddy = assignments.get(1).getCaddy();
        assignments.get(0).assignCaddy(toCaddy);
        assignments.get(1).assignCaddy(fromCaddy);
    }


    /**
     * 특정 캐디를 선택하여 배정한다.
     * 블락 시 특정캐디를 지정하기 위해 사용할 수 있다.
     */
    @Transactional
    public void assignSelectedCaddy(Long assignmentId, Long caddyId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));
        HouseCaddy caddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 캐디입니다."));

        assignment.assignCaddy(caddy);
    }

    public List<AssignmentResponse.CaddyAssignmentInfoDetail> getAssignmentInfo(List<Long> assignmentIds) {
        return assignmentRepository.findByIdsFetchJoinOrderByStartTime(assignmentIds).stream()
                .map(AssignmentResponse.CaddyAssignmentInfoDetail::from)
                .toList();
    }



    /**
     * 캐디 업무 시작 시 시작 설정, 보여줄 코스 상세 정보 조회
     */
    @Transactional
    public CourseResponse.DetailMap startAssignment(Long courseId, Long assignmentId, LocalTime startedTime) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 코스입니다."));

        if (assignment.getEndedTime() != null &&
                assignment.getAssignmentStatus() != ASSIGNED &&
                assignment.getAssignmentStatus() != BLOCKED) {
            throw new IllegalStateException("업무가 끝난 상태이거나 배정되지 않은 상태입니다.");
        }

        assignment.start(startedTime);

        return CourseResponse.DetailMap.from(course);
    }

    /**
     * 캐디 업무 종료 시 종료 설정
     */
    @Transactional
    public void terminateAssignment(Long assignmentId, LocalTime endedTime) {

        Assignment findAssignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        if (findAssignment.getStartedTime() == null &&
                findAssignment.getAssignmentStatus() != ASSIGNED &&
                findAssignment.getAssignmentStatus() != BLOCKED) {
            throw new IllegalStateException("업무가 시작하지 않은 상태이거나 배정되지 않은 상태입니다.");
        }

        findAssignment.finish(endedTime);
    }



}
