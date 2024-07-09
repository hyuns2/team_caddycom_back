package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.hole.HandicapUpdate;
import com.flash21.caddycom.dto.hole.ParUpdate;
import com.flash21.caddycom.dto.hole.SaveHoleDetail;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.repository.HoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class HoleService {
    private final HoleRepository holeRepository;

    private final TeeService teeService;
    private final TipInfoService tipInfoService;

    @Transactional
    public void updateHandicap(HandicapUpdate request) {
        Hole hole = holeRepository.findById(request.getHoleId())
                .orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        hole.updateHandicap(request.getHandicap());
    }

    @Transactional
    public void updatePar(ParUpdate request) {
        Hole hole = holeRepository.findById(request.getHoleId())
                .orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다"));

        hole.updatePar(request.getPar());
    }

    @Transactional
    public void createDetailInfo(SaveHoleDetail request) {
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
}
