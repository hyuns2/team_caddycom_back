package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.formation.CourseInfo;
import com.flash21.caddycom.dto.formation.FormationAdd;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.Tee;
import com.flash21.caddycom.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormationService {
    private final FormationRepository formationRepository;
    private final CourseRepository courseRepository;
    private final CourseJdbcRepository courseJdbcRepository;
    private final HoleRepository holeRepository;
    private final HoleJdbcRepository holeJdbcRepository;
    private final TeeRepository teeRepository;
    private final TeeJdbcRepository teeJdbcRepository;

    public void addFormation(FormationAdd request) {
        Formation formation = Formation.builder()
                .name(request.getName())
                .build();
        formationRepository.save(formation);

        List<Course> courses = new ArrayList<>();
        List<Hole> holes = new ArrayList<>();
        List<Tee> tees = new ArrayList<>();

        for(CourseInfo courseInfo : request.getCourseInfos()) {
            Course course = Course.builder()
                    .name(courseInfo.getName())
                    .totalHoles(courseInfo.getTotalHoles())
                    .formation(formation)
                    .build();
            courses.add(course);
        }
        List<Long> courseIds = courseJdbcRepository.saveAll(courses);
        List<Course> savedCourses = courseRepository.findAllById(courseIds);

        for(Course savedCourse : savedCourses) {
            for(int i = 1; i < savedCourse.getTotalHoles(); i++) {
                Hole hole = new Hole(i, savedCourse);
                holes.add(hole);
            }
        }
        List<Long> holeIds = holeJdbcRepository.saveAll(holes);
        List<Hole> savedHoles = holeRepository.findAllById(holeIds);

        for(Hole savedHole : savedHoles) {
            tees.addAll(List.of(
                    new Tee("BLACK", 320, savedHole),
                    new Tee("BLUE", 290, savedHole),
                    new Tee("WHITE", 270, savedHole),
                    new Tee("RED", 250, savedHole),
                    new Tee("GREEN", 230, savedHole)
            ));
        }
        teeJdbcRepository.saveAll(tees);
    }
}
