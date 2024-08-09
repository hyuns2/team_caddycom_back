package com.flash21.caddycom.service.golfFieldDetail;


import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.ReservationSheet;
import com.flash21.caddycom.repository.golfFieldDetail.CommentRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.golfFieldDetail.tee.TeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * 코스 정보와 관련된 CRUD
 *
 * @author Koo-EunSung
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courseRepository;
    private final HoleRepository holeRepository;
    private final TeeRepository teeRepository;
    private final CommentRepository commentRepository;

    @PersistenceContext
    private EntityManager entityManager;
    private final HoleService holeService;

    /**
     * 모든 코스 정보를 반환한다.
     *
     * @return 코스 정보 DTO 리스트
     */
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
     * 코스 정보를 생성한다.
     *
     * @param formation 코스가 포함되는 구성
     * @param requests  코스 생성 요청 DTO
     * @return 생성된 코스 id 리스트 <b>(mysql 사용 시 id가 아닌 null 반환됨)</b>
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
     * 코스에 포함된 멘트, 티, 홀과 코스를 함께 삭제한다.
     *
     * @param ids 삭제할 코스의 id 리스트
     */
    @Transactional
    public void deleteCourses(List<Long> ids) {
        courseRepository.softDeleteAllByIdInBatch(ids);
        // 코스 삭제하면 Assignment, Schedule에서 내일부터의 데이터 삭제
        LocalDate today = LocalDate.now();
        //1. Schedule 가져오기 (reservation_at이 내일부터 + 삭제된 코스를 참조하고 있는)
        List<Long> deleteScheduleId = entityManager.createQuery("SELECT s.id from Schedule s where s.reservationAt > :today and s.course.id in :courseIds", Long.class)
                .setParameter("today", today)
                .setParameter("courseIds", ids)
                .getResultList();
        //2. Assignment 삭제
        //2-1. status != ASSIGNED인 Assignment 데이터 삭제
        entityManager.createQuery("DELETE from Assignment a where a.schedule.id in :scheduleIds and a.status != :assigned")
                .setParameter("scheduleIds", deleteScheduleId)
                .setParameter("assigned", AssignmentStatus.ASSIGNED)
                .executeUpdate();
        //2-2. status == ASSIGNED인 Assignment 데이터 상태 수정 및 schedule 참조 제거
        entityManager.createQuery("UPDATE Assignment a SET a.status = :status, a.schedule = null where a.schedule.id in :scheduleIds")
                .setParameter("status", AssignmentStatus.DELETED)
                .setParameter("scheduleIds", deleteScheduleId)
                .executeUpdate();
        //3. Schedule 삭제
        entityManager.createQuery("DELETE from Schedule s where s.id in :scheduleIds")
                .setParameter("scheduleIds", deleteScheduleId)
                .executeUpdate();
        //4. ReservationSheet에서 코스 삭제
        //4-1. ReservationSheet 가져오기 (startDate > today + courseIdList에 삭제된 코스를 가지고 있는)
        List<ReservationSheet> reservationSheets = entityManager.createQuery("SELECT r from ReservationSheet r where r.startDate > :today", ReservationSheet.class)
                .setParameter("today", today)
                .getResultList();
        //4-2. reservationSheet의 courseIdList에서 삭제된 코스 id 삭제
        for (ReservationSheet rs : reservationSheets) {
            List<Long> newCourseIdList = rs.getCourseIdList().stream().flatMap(courseId -> {
                if (ids.contains(courseId))
                    return Stream.empty();
                else
                    return Stream.of(courseId);
            }).toList();
            entityManager.createQuery("UPDATE ReservationSheet r SET r.courseIdList = :updateCourseId where r.id = :id")
                    .setParameter("updateCourseId", newCourseIdList)
                    .setParameter("id", rs.getId())
                    .executeUpdate();
        }
    }


    /**
     * 구성의 포함된 코스의 모든 정보를 반환한다.
     *
     * @param formationId 구성 id
     * @return 코스 상세 정보 리스트
     */
    public List<CourseResponse.Detail> getHoles(Long formationId) {
        List<Course> courses = courseRepository.findAllByFormationId(formationId);
        return courses.stream().map(CourseResponse.Detail::from).toList();
    }
}
