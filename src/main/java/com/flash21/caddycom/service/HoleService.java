package com.flash21.caddycom.service;

import com.flash21.caddycom.dto.hole.HandicapUpdate;
import com.flash21.caddycom.dto.hole.ParUpdate;
import com.flash21.caddycom.dto.hole.SaveHoleDetail;
import com.flash21.caddycom.entity.Hole;
import com.flash21.caddycom.repository.HoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HoleService {
    private final HoleRepository holeRepository;

    private final TeeService teeService;
    private final TipInfoService tipInfoService;

    @Transactional
    public void updateHandicap(HandicapUpdate request) {
        Hole hole = holeRepository.findById(request.getHoleId())
                .orElseThrow(() -> new IllegalArgumentException("해당 홀이 존재하지 않습니다."));

        hole.updateHandicap(request.getHandicap());
    }

    @Transactional
    public void updatePar(ParUpdate request) {
        Hole hole = holeRepository.findById(request.getHoleId())
                .orElseThrow(() -> new IllegalArgumentException("해당 홀이 존재하지 않습니다"));

        hole.updatePar(request.getPar());
    }

    @Transactional
    public void processDetailInfo(SaveHoleDetail request) {
        Hole savedHole = holeRepository.findById(request.getHoleId()).orElseThrow(() -> new IllegalArgumentException("hole not found"));

        if(request.getPar() != savedHole.getPar())
            savedHole.updatePar(request.getPar());
        if(request.getHandicap() != savedHole.getHandicap())
            savedHole.updateHandicap(request.getHandicap());

        teeService.saveTees(request.getHoleId(), request.getTeeData());
        if(!request.getDeleteTeeIds().isEmpty())
            teeService.deleteTees(request.getDeleteTeeIds());

        tipInfoService.saveTipInfos(request.getHoleId(), request.getTipInfoData());
        if(!request.getDeleteTipInfoIds().isEmpty())
            tipInfoService.deleteTipInfos(request.getDeleteTipInfoIds());
    }
}
