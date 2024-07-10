package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeDto;
import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeRequest;
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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TeeService {
    private final TeeRepository teeRepository;
    private final HoleRepository holeRepository;

    @Transactional
    public void deleteAndCreateAllTee(Long courseId, TeeRequest.createAll request) {
        //1. 기존에 존재하는 모든 티 정보 삭제
        teeRepository.deleteAllByCourseId(courseId);

        //2. request 이용해서 새로운 티 정보 생성
        List<TeeRequest.create> newTeeInfos = request.getCreateInfos();
        List<Hole> holes = holeRepository.findAllByCourseId(courseId).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));
        List<Tee> newTees = new ArrayList<>();

        for(Hole hole : holes) {
            for(TeeRequest.create teeInfo : newTeeInfos) {
                newTees.add(new Tee(null, teeInfo.getName(), teeInfo.getDistance(), hole));
            }
        }

        //3. 새로운 티 정보 저장
        teeRepository.saveAllInBatch(newTees);
    }

    @Transactional
    public void createAndUpdateTees(Long holeId, List<TeeDto.info> requestTees) {
        Hole hole = holeRepository.findById(holeId).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        List<Tee> savedTees = hole.getTees();
        List<Tee> newTees = new ArrayList<>();
        for(TeeDto.info teeInfo: requestTees) {
            if(teeInfo.getId() == null) {
                newTees.add(new Tee(null, teeInfo.getName(), teeInfo.getDistance(), hole));
                break;
            }

            for(Tee savedTee : savedTees) {
                if(teeInfo.getId().equals(savedTee.getId())) {
                    savedTee.update(teeInfo.getName(), teeInfo.getDistance());
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
