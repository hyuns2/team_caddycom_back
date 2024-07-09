package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.CourseInfoResponseDto;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    final CourseRepository courseRepository;

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
}
