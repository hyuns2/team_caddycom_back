package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.CourseInfoResponseDto;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.golfFieldDetail.formation.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    final CourseRepository courseRepository;
    private final HoleService holeService;

    public List<CourseInfoResponseDto> retrieveCourseInfo() {
        List<Course> courseList = courseRepository.findAll();

        List<CourseInfoResponseDto> returnDtoList = new ArrayList<>();
        for (Course course: courseList) {
            returnDtoList.add(CourseInfoResponseDto.builder().
                    id(course.getId())
                    .name(course.getName())
                    .totalHoles(course.getTotalHoles()).build());
        }
        return returnDtoList;
    }

    public List<Course> createCourses(Formation formation, List<CourseRequest.create> requests) {
        List<Course> courses = new ArrayList<>();
        for(CourseRequest.create request : requests) {
            Course course = Course.builder()
                    .name(request.getName())
                    .totalHoles(request.getTotalHoles())
                    .formation(formation)
                    .build();
            courses.add(course);
        }

        List<Long> courseIds = courseRepository.saveAllInBatch(courses);
        List<Course> savedCourse = courseRepository.findAllById(courseIds);

        holeService.createHoles(savedCourse);
        return savedCourse;
    }

    public void updateCourse(CourseRequest.update request) {
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

    public void deleteCourses(List<Long> ids) {
        courseRepository.deleteAllByIdInBatch(ids);
    }
}
