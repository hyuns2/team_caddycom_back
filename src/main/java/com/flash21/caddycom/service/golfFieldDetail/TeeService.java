package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.tee.AllTeeSetRequest;
import com.flash21.caddycom.dto.tee.TeeData;
import com.flash21.caddycom.dto.tee.TeeUpdateRequest;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.Tee;
import com.flash21.caddycom.repository.HoleRepository;
import com.flash21.caddycom.repository.TeeJdbcRepository;
import com.flash21.caddycom.repository.TeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeeService {
    private final TeeRepository teeRepository;
    private final TeeJdbcRepository teeJdbcRepository;
    private final HoleRepository holeRepository;

    @Transactional
    public void deleteAndCreateAllTee(Long courseId, AllTeeSetRequest request) {
        //1. 기존에 존재하는 모든 티 정보 삭제
        teeRepository.deleteAllByCourseId(courseId);

        //2. request 이용해서 새로운 티 정보 생성
        List<TeeUpdateRequest> newTeeInfos = request.getTeeInfos();
        List<Hole> holes = holeRepository.findAllByCourseId(courseId).orElseThrow(() -> new RuntimeException("hole not found"));
        List<Tee> newTees = new ArrayList<>();

        for(Hole hole : holes) {
            for(TeeUpdateRequest teeInfo : newTeeInfos) {
                newTees.add(new Tee(teeInfo.getTeeName(), teeInfo.getDistance(), hole));
            }
        }

        //3. 새로운 티 정보 저장
        teeJdbcRepository.saveAll(newTees);
    }

    @Transactional
    public void createAndUpdateTees(Long holeId, List<TeeData> requestTees) {
        Hole hole = holeRepository.findById(holeId).orElseThrow(() -> new IllegalArgumentException("hole not found"));

        List<Tee> savedTees = hole.getTees();
        List<Tee> newTees = new ArrayList<>();
        for(TeeData teeData : requestTees) {
            if(teeData.getId() == null) {
                newTees.add(new Tee(teeData.getName(), teeData.getDistance(), hole));
                break;
            }

            for(Tee savedTee : savedTees) {
                if(teeData.getId().equals(savedTee.getId())) {
                    savedTee.teeUpdate(teeData.getName(), teeData.getDistance());
                    break;
                }
            }
        }

        savedTees.addAll(newTees);
    }

    @Transactional
    public void deleteTees(List<Long> teeIds) {
        teeRepository.deleteAllByIdInBatch(teeIds);
    }
}
