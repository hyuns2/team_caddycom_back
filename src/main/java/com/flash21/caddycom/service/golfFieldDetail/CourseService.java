package com.flash21.caddycom.service.golfFieldDetail;


import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import com.flash21.caddycom.repository.golfFieldDetail.CommentRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.golfFieldDetail.tee.TeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;
    private final HoleRepository holeRepository;
    private final TeeRepository teeRepository;
    private final CommentRepository commentRepository;

    private final HoleService holeService;

    /**
     * 모든 코스 정보를 반환한다.
     *
     * @return 코스 정보 DTO 리스트
     */
    public List<CourseResponse.Info> retrieveCourseInfo() {
        List<Course> courseList = courseRepository.findAll();

        List<CourseResponse.Info> returnDtoList = new ArrayList<>();
        for (Course course: courseList) {
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
     * @param requests 코스 생성 요청 DTO
     * @return 생성된 코스 id 리스트 <b>(mysql 사용 시 id가 아닌 null 반환됨)</b>
     */
    public List<Long> createCourses(Formation formation, List<CourseRequest.Create> requests) {
        List<Course> courses = new ArrayList<>();
        for(CourseRequest.Create request : requests) {
            if(request.getName() == null || request.getName().isBlank())
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
     * @throws NoSuchElementException
     *          수정하려는 코스가 없는 경우
     * @throws IllegalArgumentException
     *          코스의 이름을 공백으로 수정하려는 경우
     */
    public void updateCourse(CourseRequest.Update request) {
        Course course = courseRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 코스는 존재하지 않습니다."));

        String name = request.getName();
        if(name != null) {
            if(name.isBlank())
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
    public void deleteCourses(List<Long> ids) {
        teeRepository.deleteAllByCourseIds(ids);
        commentRepository.deleteAllByCourseIds(ids);
        holeRepository.deleteAllByCourseIds(ids);
        courseRepository.deleteAllByIdInBatch(ids);
    }


    /**
     * 구성의 포함된 코스의 모든 정보를 반환한다.
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
