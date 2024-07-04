package com.flash21.caddycom.service;

import com.flash21.caddycom.dto.formation.CourseInfo;
import com.flash21.caddycom.dto.formation.FormationAdd;
import com.flash21.caddycom.entity.Course;
import com.flash21.caddycom.entity.Formation;
import com.flash21.caddycom.repository.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormationService {
    private final FormationRepository formationRepository;

    public void addFormation(FormationAdd request) {
        Formation formation = Formation.builder()
                .name(request.getName())
                .build();

        for(CourseInfo courseInfo : request.getCourseInfos()) {
            Course course = Course.builder()
                    .name(courseInfo.getName())
                    .totalHoles(courseInfo.getTotalHoles())
                    .formation(formation)
                    .build();
            formation.getCourses().add(course);
        }

        formationRepository.save(formation);
    }
}
