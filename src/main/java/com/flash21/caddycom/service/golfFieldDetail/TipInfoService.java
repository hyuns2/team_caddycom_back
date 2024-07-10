package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.tipInfo.TipInfoDto;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.TipInfo;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.TipInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TipInfoService {
    private final TipInfoRepository tipInfoRepository;
    private final HoleRepository holeRepository;

    @Transactional
    public void createAndUpdateTipInfos(Long holeId, List<TipInfoDto.info> tipInfos) {
        Hole hole = holeRepository.findById(holeId).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        List<TipInfo> savedTipInfos = hole.getTipInfos();
        List<TipInfo> newTipInfos = new ArrayList<>();
        for(TipInfoDto.info info : tipInfos) {
            if(info.getId() == null) {
                newTipInfos.add(new TipInfo(null, info.getTitle(), info.getContent(), hole));
                break;
            }

            for(TipInfo tipInfo : savedTipInfos) {
                if(info.getId().equals(tipInfo.getId())) {
                    tipInfo.update(info.getTitle(), info.getContent());
                    break;
                }
            }
        }

        savedTipInfos.addAll(newTipInfos);
    }

    @Transactional
    public void deleteTipInfos(List<Long> tipInfoIds) {
        tipInfoRepository.deleteAllByIdInBatch(tipInfoIds);
    }
}
