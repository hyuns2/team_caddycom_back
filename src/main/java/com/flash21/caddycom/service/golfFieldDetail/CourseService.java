package com.flash21.caddycom.service.golfFieldDetail;


import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.ReservationSheet;
import com.flash21.caddycom.entity.schedule.Schedule;
import com.flash21.caddycom.repository.assignment.AssignmentRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.schedule.ReservationSheetRepository;
import com.flash21.caddycom.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 코스 정보와 관련된 CRUD
 *
 * @author Koo-EunSung
 */
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final ScheduleRepository scheduleRepository;
    private final AssignmentRepository assignmentRepository;
    private final ReservationSheetRepository reservationSheetRepository;

    private final HoleService holeService;
    private final HoleRepository holeRepository;
    private final TeeService teeService;

    /**
     * 골프장의 모든 코스 정보를 반환한다.
     *
     * @param golfFieldId 골프장 id
     * @return 코스 정보 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<CourseResponse.Info> retrieveCourseInfo(Long golfFieldId) {
        List<Course> courseList = courseRepository.findAllByGolfFieldId(golfFieldId);

        List<CourseResponse.Info> returnDtoList = new ArrayList<>();
        for (Course course : courseList) {
            returnDtoList.add(CourseResponse.Info.builder().
                    id(course.getId())
                    .name(course.getName())
                    .totalHoles(course.getTotalHoles()).build());
        }
        return returnDtoList;
    }

    /**
     * 코스 정보를 생성한다. 이때 홀과 티 정보도 같이 생성한다.
     *
     * @param formation 코스가 포함되는 구성
     * @param requests  코스 생성 요청 DTO
     * @throws IllegalArgumentException 코스의 이름을 공백으로 생성하려는 경우
     * @return 생성된 코스 id 리스트 <b>(mysql 사용 시 id가 아닌 null 반환됨)</b>
     * @see HoleService#createHoles(List)
     * @see TeeService#createTees(List)
     */
    @Transactional
    public List<Long> createCourses(Formation formation, List<CourseRequest.Create> requests) {
        List<Course> courses = new ArrayList<>();
        for (CourseRequest.Create request : requests) {
            if (request.getName() == null || request.getName().isBlank())
                throw new IllegalArgumentException("코스의 이름은 공백일 수 없습니다.");

            Course course = Course.builder()
                    .name(request.getName())
                    .totalHoles(request.getTotalHoles())
                    .formation(formation)
                    .build();
            courses.add(course);
        }

        List<Long> courseIds = courseRepository.saveAllInBatch(courses);
        List<Course> createdCourses = courseRepository.findAllByFormationIdAndHolesIsEmpty(formation.getId());
        holeService.createHoles(createdCourses);

        List<Long> createdCourseIds = new ArrayList<>();
        for(Course course : createdCourses)
            createdCourseIds.add(course.getId());

        List<Hole> holes = holeRepository.findAllByCourseIdsAndTeesIsEmpty(createdCourseIds);

        teeService.createTees(holes);

        return courseIds;
    }

    /**
     * 코스 정보를 수정한다. 이때 수정되는 내용에 따라 홀 정보도 수정될 수 있다. <br>
     * 코스의 홀 수가 늘어날 경우 - 홀 정보 생성 <br>
     * 코스의 홀 수가 줄어들 경우 - 홀 정보 삭제
     *
     * @param request 코스 수정 요청 DTO
     * @throws NoSuchElementException   수정하려는 코스가 없는 경우
     * @throws IllegalArgumentException 코스의 이름을 공백으로 수정하려는 경우
     */
    @Transactional
    public void updateCourse(CourseRequest.Update request) {
        Course course = courseRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 코스는 존재하지 않습니다."));

        String name = request.getName();
        if (name != null) {
            if (name.isBlank())
                throw new IllegalArgumentException("코스의 이름은 공백일 수 없습니다.");
            course.updateName(name);
        }

        Integer newTotalHoles = request.getTotalHoles();
        if (newTotalHoles != null) {
            Integer savedTotalHoles = course.getTotalHoles();
            course.updateTotalHoles(newTotalHoles);
            if (newTotalHoles > savedTotalHoles) //홀 수가 늘어나는 경우 => 홀 생성
                holeService.createHoles(List.of(course));
            else if (newTotalHoles < savedTotalHoles) //홀 수가 줄어드는 경우 => 홀 삭제
                holeService.deleteHoles(List.of(course));
        }
    }

    /**
     * 코스를 삭제한다. <br>
     * 코스 삭제 시 Schedule과 Assignment 정보를 함께 삭제한다. 또한 ReservationSheet의 코스 id 리스트에서 해당 코스를 삭제한다. <br>
     * 미래의 일정 중 블락되었거나 캐디가 배정된 일정이 있을 경우 예외를 던진다.
     *
     * @param ids 삭제할 코스의 id 리스트
     * @throws IllegalArgumentException 블락되었거나 캐디가 배정된 일정이 있을 경우
     */
    @Transactional
    public void deleteCourses(List<Long> ids) {
        courseRepository.softDeleteAllByIdInBatch(ids);
        // 코스 삭제하면 Assignment, Schedule에서 내일부터의 데이터 삭제
        LocalDate today = LocalDate.now();
        //1. Schedule 가져오기 (reservation_at이 내일부터 + 삭제된 코스를 참조하고 있는)
        List<Schedule> deleteSchedule = scheduleRepository.findAllByCourseIdsAfterDate(ids, today);
        if(!deleteSchedule.isEmpty()) {
            //2. Assignment 삭제
            //2-1. Assignment 검사 - 블락되었거나, 캐디가 배정된 일정이 있는지
            if (!assignmentRepository.findByStatusAndSchedule(deleteSchedule, AssignmentStatus.ASSIGNED, AssignmentStatus.BLOCKED, PageRequest.of(0, 1)).isEmpty()) {
                throw new IllegalArgumentException("블락되었거나 캐디가 배정된 일정이 있는 코스는 삭제할 수 없습니다.");
            }
            //2-2. Assignment 삭제
            assignmentRepository.deleteAllBySchedules(deleteSchedule);

            //3. Schedule 삭제
            scheduleRepository.deleteAllInBatch(deleteSchedule);
        }
        //4. ReservationSheet에서 코스 삭제
        //4-1. ReservationSheet 가져오기 (startDate > today + courseIdList에 삭제된 코스를 가지고 있는)
        List<ReservationSheet> reservationSheets = reservationSheetRepository.findAllByAfterDate(today);
        if(!reservationSheets.isEmpty()) {
            //4-2. reservationSheet의 courseIdList에서 삭제된 코스 id 삭제
            for (ReservationSheet rs : reservationSheets)
                rs.removeCourse(ids);
        }

    }


    /**
     * 구성에 포함된 코스의 모든 정보(홀, 티, 멘트 정보 포함)를 반환한다.
     *
     * @param formationId 구성 id
     * @return 코스 상세 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<CourseResponse.Detail> getHoles(Long formationId) {
        List<Course> courses = courseRepository.findAllByFormationId(formationId);
        return courses.stream().map(CourseResponse.Detail::from).toList();
    }
}
