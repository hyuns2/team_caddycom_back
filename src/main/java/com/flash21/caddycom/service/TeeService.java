package com.flash21.caddycom.service;

import com.flash21.caddycom.dto.AllTeeSetRequest;
import com.flash21.caddycom.dto.TeeUpdateRequest;
import com.flash21.caddycom.entity.Hole;
import com.flash21.caddycom.entity.Tee;
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
    public void setAllTee(Long courseId, AllTeeSetRequest request) {
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
}
