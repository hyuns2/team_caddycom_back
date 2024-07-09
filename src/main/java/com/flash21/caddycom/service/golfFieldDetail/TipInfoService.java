package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.tipInfo.TipInfoData;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.TipInfo;
import com.flash21.caddycom.repository.HoleRepository;
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
    public void createAndUpdateTipInfos(Long holeId, List<TipInfoData> tipInfoDatas) {
        Hole hole = holeRepository.findById(holeId).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        List<TipInfo> savedTipInfos = hole.getTipInfos();
        List<TipInfo> newTipInfos = new ArrayList<>();
        for(TipInfoData data : tipInfoDatas) {
            if(data.getId() == null) {
                newTipInfos.add(new TipInfo(null, data.getTitle(), data.getContent(), hole));
                break;
            }

            for(TipInfo tipInfo : savedTipInfos) {
                if(data.getId().equals(tipInfo.getId())) {
                    tipInfo.update(data.getTitle(), data.getContent());
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
