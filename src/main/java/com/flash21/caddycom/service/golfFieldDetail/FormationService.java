package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseDto;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
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
import jakarta.transaction.Transactional;
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
    private final CourseService courseService;

    public void createFormation(GolfField golfField, FormationRequest.create request) {
        Formation formation;
        if(request.getName() != null)
            formation = new Formation(null, golfField, request.getName(), null);
        else
            formation = new Formation(null, golfField, "NONE", null);

        formationRepository.save(formation);

        courseService.createCourses(formation, request.getCourseInfos());
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
        formationRepository.deleteAllByIdInBatch(ids);
    }
}
