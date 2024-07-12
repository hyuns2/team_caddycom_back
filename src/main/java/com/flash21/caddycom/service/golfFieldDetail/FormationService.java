package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseDto;
import com.flash21.caddycom.dto.golfFieldDetail.formation.FormationRequest;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.Tee;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.golfFieldDetail.formation.FormationRepository;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.golfFieldDetail.tee.TeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FormationService {
    private final GolfFieldRepository golfFieldRepository;
    private final FormationRepository formationRepository;
    private final CourseRepository courseRepository;
    private final HoleRepository holeRepository;
    private final TeeRepository teeRepository;

    public void createFormation(FormationRequest.create request) {
        GolfField golfField = golfFieldRepository.findById(request.getGolfFieldId())
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));
        Formation formation = new Formation(null, golfField, request.getName(), null);
        formationRepository.save(formation);

        List<Course> courses = new ArrayList<>();
        List<Hole> holes = new ArrayList<>();
        List<Tee> tees = new ArrayList<>();

        for (CourseDto.info courseInfo : request.getCourseInfos()) {
            Course course = Course.builder()
                    .name(courseInfo.getName())
                    .totalHoles(courseInfo.getTotalHoles())
                    .formation(formation)
                    .build();
            courses.add(course);
        }
        List<Long> courseIds = courseRepository.saveAllInBatch(courses);
        List<Course> savedCourses = courseRepository.findAllById(courseIds);

        for (Course savedCourse : savedCourses) {
            for (int i = 1; i <= savedCourse.getTotalHoles(); i++) {
                Hole hole = new Hole(i, savedCourse);
                holes.add(hole);
            }
        }
        List<Long> holeIds = holeRepository.saveAllInBatch(holes);
        List<Hole> savedHoles = holeRepository.findAllById(holeIds);

        for (Hole savedHole : savedHoles) {
            tees.addAll(List.of(
                    new Tee(null, "BLACK", 320, savedHole),
                    new Tee(null, "BLUE", 290, savedHole),
                    new Tee(null, "WHITE", 270, savedHole),
                    new Tee(null, "RED", 250, savedHole),
                    new Tee(null, "GREEN", 230, savedHole)
            ));
        }
        teeRepository.saveAllInBatch(tees);
    }
}
