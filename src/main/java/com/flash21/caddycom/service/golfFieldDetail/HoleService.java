package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleRequest;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
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
public class HoleService {
    private final HoleRepository holeRepository;
    private final TeeService teeService;
    private final CommentService commentService;
    private final TeeRepository teeRepository;

    @Transactional
    public void updateHandicap(HoleRequest.updateHandicap request) {
        Hole hole = holeRepository.findById(request.getHoleId())
                .orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        hole.updateHandicap(request.getHandicap());
    }

    @Transactional
    public void updatePar(HoleRequest.updatePar request) {
        Hole hole = holeRepository.findById(request.getHoleId())
                .orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다"));

        hole.updatePar(request.getPar());
    }

    @Transactional
    public List<Long> createHoles(List<Course> courses) {
        List<Hole> holes = new ArrayList<>();
        for(Course course : courses) {
            for(int i = course.getHoles().size() + 1; i <= course.getTotalHoles(); i++) {
                Hole hole = new Hole(i, course);
                holes.add(hole);
            }
        }
        List<Long> holeIds = holeRepository.saveAllInBatch(holes);
        return holeIds;
    }

    @Transactional
    public void createDetailInfo(HoleRequest.createDetailInfo request) {
        Hole savedHole = holeRepository.findById(request.getHoleId()).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        if(request.getPar() != savedHole.getPar())
            savedHole.updatePar(request.getPar());
        if(request.getHandicap() != savedHole.getHandicap())
            savedHole.updateHandicap(request.getHandicap());

        teeService.createAndUpdateTees(request.getHoleId(), request.getTeeData());
        if(!request.getDeleteTeeIds().isEmpty())
            teeService.deleteTees(request.getDeleteTeeIds());

        commentService.createAndUpdateTipInfos(request.getHoleId(), request.getTipInfoData());
        if(!request.getDeleteTipInfoIds().isEmpty())
            commentService.deleteTipInfos(request.getDeleteTipInfoIds());
    }

    @Transactional
    public void deleteHoles(List<Course> courses) {
        List<Hole> deleteHoles = new ArrayList<>();
        for(Course course : courses) {
            for (Hole hole : course.getHoles()) {
                if (hole.getNum() > course.getTotalHoles())
                    deleteHoles.add(hole);
            }
        }
        teeRepository.deleteAllByHoles(deleteHoles);
        holeRepository.deleteAllInBatch(deleteHoles);
    }
}
