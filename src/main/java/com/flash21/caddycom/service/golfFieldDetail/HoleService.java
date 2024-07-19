package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleRequest;
import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleResponse;
import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeDto;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.Tee;
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
    private final TipInfoService tipInfoService;
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
    public List<Hole> createHoles(List<Course> courses) {
        List<Hole> holes = new ArrayList<>();
        for(Course course : courses) {
            for(int i = course.getHoles().size() + 1; i <= course.getTotalHoles(); i++) {
                Hole hole = new Hole(i, course);
                holes.add(hole);
            }
        }
        List<Long> holeIds = holeRepository.saveAllInBatch(holes);
        List<Hole> saveHoles = holeRepository.findAllById(holeIds);
        teeService.createTees(saveHoles);
        return saveHoles;
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

        tipInfoService.createAndUpdateTipInfos(request.getHoleId(), request.getTipInfoData());
        if(!request.getDeleteTipInfoIds().isEmpty())
            tipInfoService.deleteTipInfos(request.getDeleteTipInfoIds());
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

    @Transactional(readOnly = true)
    public List<HoleResponse.Info> getHoles(Long courseId) {
        List<Hole> holes = holeRepository.findAllByCourseIdFetchJoinTee(courseId)
                .orElseThrow(() -> new NoSuchElementException("해당 코스에 홀이 존재하지 않습니다."));
        List<HoleResponse.Info> response = new ArrayList<>();
        for(Hole hole : holes) {
            List<TeeDto.Info> teeInfos = new ArrayList<>();
            for(Tee tee : hole.getTees()) {
                teeInfos.add(new TeeDto.Info(tee.getId(), tee.getName(), tee.getDistance()));
            }
            response.add(new HoleResponse.Info(hole.getId(), hole.getNum(), hole.getPar(), hole.getHandicap(), teeInfos));
        }

        return response;
    }
}
