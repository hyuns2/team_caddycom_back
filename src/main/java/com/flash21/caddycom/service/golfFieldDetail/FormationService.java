package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.dto.golfFieldDetail.formation.FormationRequest;
import com.flash21.caddycom.dto.golfFieldDetail.formation.FormationResponse;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.golfFieldDetail.comment.CommentRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.golfFieldDetail.formation.FormationRepository;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.golfFieldDetail.tee.TeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 골프장 구성과 관련된 CRUD
 *
 * @author Koo-EunSung
 */
@Service
@RequiredArgsConstructor
public class FormationService {
    private final FormationRepository formationRepository;
    private final CourseRepository courseRepository;
    private final HoleRepository holeRepository;
    private final TeeRepository teeRepository;
    private final CommentRepository commentRepository;
    private final GolfFieldRepository golfFieldRepository;

    private final ObjectProvider<FormationService> formationServiceProvider;
    private final CourseService courseService;
    private final HoleService holeService;
    private final TeeService teeService;

    public void processCreate(FormationRequest.Process request) {
        GolfField golfField = golfFieldRepository.findById(request.getGolfFieldId()).orElseThrow(() -> new NoSuchElementException("해당 골프장이 존재하지 않습니다."));

        final FormationService formationService = formationServiceProvider.getObject();
        if (request.getCreate() != null) for (FormationRequest.Create create : request.getCreate())
            formationService.createFormation(golfField, create);

        if (request.getUpdate() != null) for (FormationRequest.Update update : request.getUpdate())
            formationService.updateFormation(update);

    }

    /**
     * 구성 정보를 생성하면서 코스, 홀, 티의 정보를 같이 생성한다.
     *
     * @param golfField 구성을 추가할 골프장의 id. null일 수 없다.
     * @param request   구성 생성 요청 DTO
     */
    @Transactional
    public void createFormation(GolfField golfField, FormationRequest.Create request) {
        Formation formation;
        if (request.getName() == null || request.getName().isBlank())
            formation = new Formation(null, golfField, "NONE", null);
        else formation = new Formation(null, golfField, request.getName(), null);

        formationRepository.save(formation);

        courseService.createCourses(formation, request.getCourseInfos());
    }

    /**
     * 구성 정보를 수정한다. 구성에 포함된 코스 정보도 포함된다.
     *
     * @param request 구성 정보 수정 요청 DTO
     * @throws NoSuchElementException   수정하려는 구성이 존재하지 않을 경우
     * @throws IllegalArgumentException 구성의 이름을 공백으로 수정하려는 경우
     */
    @Transactional
    public void updateFormation(FormationRequest.Update request) {
        Formation formation = formationRepository.findById(request.getFormationId()).orElseThrow(() -> new NoSuchElementException("해당 구성은 존재하지 않습니다."));

        String name = request.getFormationName();
        if (name != null) {
            if (name.isBlank()) throw new IllegalArgumentException("구성의 이름은 공백일 수 없습니다.");
            formation.updateName(name);
        }

        List<CourseRequest.Update> courseUpdateInfos = request.getCourseInfos();
        List<CourseRequest.Create> courseCreateInfos = new ArrayList<>();
        if (courseUpdateInfos != null) {
            for (CourseRequest.Update courseUpdateInfo : courseUpdateInfos) {
                if (courseUpdateInfo.getId() == 0)
                    courseCreateInfos.add(new CourseRequest.Create(courseUpdateInfo.getName(), courseUpdateInfo.getTotalHoles()));
                else courseService.updateCourse(courseUpdateInfo);
            }
            if (!courseCreateInfos.isEmpty()) {
                courseService.createCourses(formation, courseCreateInfos);
            }
        }
    }

    /**
     * 구성에 포함된 멘트, 티, 홀, 코스와 구성 정보를 함께 삭제한다.
     *
     * @param ids 삭제할 구성의 id 리스트
     */
    @Transactional
    public void deleteFormations(List<Long> ids) {
        List<Long> courseIds = courseRepository.findAllIdByFormationIds(ids);
        try {
            courseService.deleteCourses(courseIds);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("블락되었거나 캐디가 배정된 일정이 있는 코스가 포함된 구성은 삭제할 수 없습니다.");
        }
        formationRepository.deleteAllByIdInBatch(ids);
    }

    /**
     * 골프장의 모든 구성 정보를 반환한다.
     *
     * @param golfFieldId 구성 정보를 조회할 골프장의 id. null일 수 없다.
     * @return 골프장의 모든 구성 정보
     * @throws NoSuchElementException 골프장에 구성이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public List<FormationResponse.Info> getAllFormations(Long golfFieldId) {
        List<Formation> formations = formationRepository.findAllByGolfFieldId(golfFieldId);
        if (formations.isEmpty()) throw new NoSuchElementException("골프장에 구성이 존재하지 않습니다.");

        List<FormationResponse.Info> response = new ArrayList<>();
        for (Formation formation : formations) {
            List<CourseResponse.Info> courseInfos = new ArrayList<>();
            if (!formation.getCourses().isEmpty()) {
                courseInfos = formation.getCourses().stream()
                        .map(course -> new CourseResponse.Info(course.getId(), course.getName(), course.getTotalHoles()))
                        .sorted(Comparator.comparingLong(CourseResponse.Info::getId))
                        .collect(Collectors.toList());
            }
            response.add(new FormationResponse.Info(formation.getId(), formation.getName(), courseInfos));
        }
        return response;
    }
}
