package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.dto.golfFieldDetail.formation.FormationRequest;
import com.flash21.caddycom.dto.golfFieldDetail.formation.FormationResponse;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.repository.golfFieldDetail.CommentRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.golfFieldDetail.formation.FormationRepository;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.golfFieldDetail.tee.TeeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class FormationService {
    private final FormationRepository formationRepository;
    private final CourseRepository courseRepository;
    private final HoleRepository holeRepository;
    private final TeeRepository teeRepository;
    private final CommentRepository commentRepository;

    private final CourseService courseService;
    private final HoleService holeService;
    private final TeeService teeService;

    public void createFormation(GolfField golfField, FormationRequest.create request) {
        Formation formation;
        if(request.getName() == null || request.getName().isBlank())
            formation = new Formation(null, golfField, "NONE", null);
        else
            formation = new Formation(null, golfField, request.getName(), null);

        formationRepository.save(formation);

        courseService.createCourses(formation, request.getCourseInfos());
        List<Course> courses = courseRepository.findAllByFormationId(formation.getId());

        holeService.createHoles(courses);

        List<Long> courseIds = new ArrayList<>();
        for(Course course : courses)
            courseIds.add(course.getId());

        List<Hole> holes = holeRepository.findAllByCourseIds(courseIds).orElseThrow(() -> new NoSuchElementException("해당 코스가 존재하지 않습니다."));

        teeService.createTees(holes);
    }

    public void updateFormation(FormationRequest.update request) {
        Formation formation = formationRepository.findById(request.getFormationId())
                .orElseThrow(() -> new NoSuchElementException("해당 구성은 존재하지 않습니다."));

        String name = request.getFormationName();
        if (name != null) {
            if (name.isBlank())
                throw new IllegalArgumentException("구성의 이름은 공백일 수 없습니다.");
            formation.updateName(name);
        }

        List<CourseRequest.update> courseUpdateInfos = request.getCourseInfos();
        List<CourseRequest.create> courseCreateInfos = new ArrayList<>();
        if(courseUpdateInfos != null) {
            for(CourseRequest.update courseUpdateInfo : courseUpdateInfos) {
                if (courseUpdateInfo.getId() == null)
                    courseCreateInfos.add(
                            new CourseRequest.create(courseUpdateInfo.getName(), courseUpdateInfo.getTotalHoles())
                    );
                else
                    courseService.updateCourse(courseUpdateInfo);
            }
            if (!courseCreateInfos.isEmpty())
                courseService.createCourses(formation, courseCreateInfos);
        }
    }

    public void deleteFormations(List<Long> ids) {
        commentRepository.deleteAllByFormationIds(ids);
        teeRepository.deleteAllByFormationIds(ids);
        holeRepository.deleteAllByFormationIds(ids);
        courseRepository.deleteAllByFormationIds(ids);
        formationRepository.deleteAllByIdInBatch(ids);
    }

    public List<FormationResponse.create> getAllFormations(Long golfFieldId) {
        List<Formation> formations = formationRepository.findAllByGolfFieldId(golfFieldId)
                .orElseThrow(() -> new NoSuchElementException("골프장에 구성이 존재하지 않습니다."));

        List<FormationResponse.create> response = new ArrayList<>();
        for(Formation formation : formations) {
            List<CourseResponse.Create> courseInfos = new ArrayList<>();
            for (Course course : formation.getCourses()) {
                courseInfos.add(new CourseResponse.Create(course.getId(), course.getName(), course.getTotalHoles()));
            }
            response.add(new FormationResponse.create(formation.getId(), formation.getName(), courseInfos));
        }
        return response;
    }
}
