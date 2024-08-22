package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeResponse;
import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeRequest;
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

/**
 * 티 정보와 관련된 CRUD
 *
 * @author Koo-EunSung
 */
@Service
@RequiredArgsConstructor
public class TeeService {
    private final TeeRepository teeRepository;
    private final HoleRepository holeRepository;

    /**
     * 코스 내 모든 홀의 티를 일괄적으로 생성한다.
     *
     * @param courseId 코스 id
     * @param request 설정할 티 정보
     * @throws NoSuchElementException 코스에 홀이 존재하지 않을 경우
     */
    @Transactional
    public void deleteAndCreateAllTee(Long courseId, TeeRequest.CreateAll request) {
        //1. 기존에 존재하는 모든 티 정보 삭제
        teeRepository.deleteAllByCourseId(courseId);

        //2. request 이용해서 새로운 티 정보 생성
        List<TeeRequest.Create> newTeeInfos = request.getCreateInfos();
        List<Hole> holes = holeRepository.findAllByCourseId(courseId);
        if(holes.isEmpty())
            throw new NoSuchElementException("해당 코스에 홀이 존재하지 않습니다.");
        List<Tee> newTees = new ArrayList<>();

        for(Hole hole : holes) {
            for(TeeRequest.Create teeInfo : newTeeInfos) {
                newTees.add(new Tee(null, teeInfo.getName(), teeInfo.getDistance(), hole));
            }
        }

        //3. 새로운 티 정보 저장
        teeRepository.saveAllInBatch(newTees);
    }

    /**
     * 홀의 티 정보를 생성하거나 수정한다.
     *
     * @param holeId 티 정보를 설정한 홀의 id
     * @param requestTees 설정할 티 정보
     * @throws NoSuchElementException 홀이 존재하지 않을 경우
     */
    @Transactional
    public void createAndUpdateTees(Long holeId, List<TeeResponse.Info> requestTees) {
        if (requestTees == null || requestTees.isEmpty()) return;

        Hole hole = holeRepository.findById(holeId).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        List<Tee> savedTees = hole.getTees();
        List<Tee> newTees = new ArrayList<>();
        for(TeeResponse.Info teeInfo: requestTees) {
            if(teeInfo.getId() == 0) {
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

    /**
     * 홀의 티를 기본값으로 일괄적으로 생성한다. 홀 생성 시에만 호출된다.
     *
     * @param holes 홀 리스트
     * @return 생성된 티의 id 리스트
     */
    @Transactional
    public List<Long> createTees(List<Hole> holes) {
        List<Tee> tees = new ArrayList<>();
        for(Hole hole : holes) {
            tees.addAll(List.of(
                    new Tee(null, "BLACK", 320, hole),
                    new Tee(null, "BLUE", 290, hole),
                    new Tee(null, "WHITE", 270, hole),
                    new Tee(null, "RED", 250, hole),
                    new Tee(null, "GREEN", 230, hole)
            ));
        }

        return teeRepository.saveAllInBatch(tees);
    }

    /**
     * 티를 삭제한다.
     *
     * @param teeIds 삭제할 티의 id 리스트
     */
    @Transactional
    public void deleteTees(List<Long> teeIds) {
        if(teeIds == null || teeIds.isEmpty()) return;
        teeRepository.deleteAllByIdInBatch(teeIds);
    }
}
