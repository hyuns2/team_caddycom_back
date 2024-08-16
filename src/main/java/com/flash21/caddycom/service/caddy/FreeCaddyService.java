package com.flash21.caddycom.service.caddy;

import com.flash21.caddycom.dto.caddy.FreeCaddyCommand;
import com.flash21.caddycom.dto.caddy.FreeCaddyResponse;
import com.flash21.caddycom.dto.golfField.GolfFieldResponse;
import com.flash21.caddycom.dto.schedule.ScheduleResponse;
import com.flash21.caddycom.entity.caddy.FreeCaddy;
import com.flash21.caddycom.entity.caddy.MatchedFreeCaddy;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.Schedule;
import com.flash21.caddycom.global.common.fileUploader.FileUploader;
import com.flash21.caddycom.repository.caddy.FreeCaddyRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FreeCaddyService {
    private final FreeCaddyRepository freeCaddyRepository;
    private final GolfFieldRepository golfFieldRepository;
    private final FileUploader fileUploader;
    private final PlatformTransactionManager transactionManager;
    private final ScheduleRepository scheduleRepository;


    /**
     * 1. 전화번호로 FreeCaddy를 찾는다.
     * 2. 요청된 지정 골프장 중 추가될 것만 추출한다.
     * 3. 프로필 이미지를 업로드 한다.
     * 4. 요청값으로 FreeCaddy를 저장한다.
     */
    public void saveFreeCaddy(FreeCaddyCommand.Create command) {
        FreeCaddy freeCaddy = freeCaddyRepository.findByPhoneNumber(command.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("앞서 전화번호 인증이 되지 않아 로그인이 제대로 이뤄지지 않았습니다."));

        List<MatchedFreeCaddy> matchedFreeCaddyList = getDistinctGolfField(freeCaddy, command.getGolfFieldIds());
        String profileUrl = fileUploader.upload(command.getProfileUrl(), "caddy");

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            saveEntity(command, freeCaddy, matchedFreeCaddyList, profileUrl);
            return null;
        });
    }

    protected void saveEntity(FreeCaddyCommand.Create request,
                              FreeCaddy freeCaddy,
                              List<MatchedFreeCaddy> matchedFreeCaddyList,
                              String profileUrl)
    {
        freeCaddy.create(request.getName(),
                request.getPhoneNumber(),
                request.getRegions(),
                request.getGender(),
                request.getBirth(),
                request.getCareer(),
                request.getIntro(),
                matchedFreeCaddyList,
                profileUrl);
    }



    /**
     * 프리캐디 등록 시 지정골프장을 추가하기 위함. (중복 제외)
     *
     * 1. 요청된 지정 골프장이 1개 이상인지 확인한다.
     * 2. 요청된 지정 골프장 중 존재하지 않는것이 있는지 확인한다.
     * 3. 요청된 것중 이미 매칭된 골프장은 제외한다.
     * 4. MatchedFreeCaddy 목록을 반환한다.
     */
    private List<MatchedFreeCaddy> getDistinctGolfField(FreeCaddy freeCaddy, List<Long> golfFieldIds) {
        if (golfFieldIds == null || golfFieldIds.isEmpty())
            throw new IllegalArgumentException("지정골프장은 최소 1개 이상이어야 합니다.");

        List<GolfField> golfFieldList = golfFieldRepository.findByIds(golfFieldIds);
        if (golfFieldList.size() != golfFieldIds.size())
            throw new IllegalArgumentException("존재하지 않는 골프장이 포함되어 있습니다.");

        Set<Long> existingGolfFieldSet = freeCaddy.getMatchedFreeCaddyList().stream()
                .map(matchedFreeCaddy -> matchedFreeCaddy.getGolfField().getId())
                .collect(Collectors.toSet());

        List<MatchedFreeCaddy> matchedFreeCaddyList = golfFieldList.stream()
                .filter(golfField -> !existingGolfFieldSet.contains(golfField.getId()))
                .map(golfField -> MatchedFreeCaddy.of(freeCaddy, golfField))
                .toList();

        return matchedFreeCaddyList;
    }


    /**
     * 프리캐디 상세 조회
     */
    @Transactional(readOnly = true)
    public FreeCaddyResponse.Info getFreeCaddy(Long id) {
        FreeCaddy freeCaddy = freeCaddyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 프리캐디입니다."));
        return FreeCaddyResponse.Info.from(freeCaddy);
    }


    @Transactional(readOnly = true)
    public List<GolfFieldResponse.WithFreeCaddy> getMatchedGolfField(Long caddyId) {
        FreeCaddy freeCaddy = freeCaddyRepository.findById(caddyId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 프리캐디입니다."));

        return freeCaddy.getMatchedFreeCaddyList().stream()
                .map(GolfFieldResponse.WithFreeCaddy::from)
                .toList();
    }

    /**
     * 프리캐디 지정골프장의 미배정 목록 조회
     *
     * 1. 지정골프장의 스케줄을 조회한다.
     * 2. 스케줄을 날짜별로 그룹핑한다.
     * 2-1. assignmentMap (key: 날짜, value: 미배정 목록)
     * 2-2. countMap (key: 날짜, value: 미배정 수)
     * 3. List< 날짜 , 미배정 수, List<미배정 Item> > 형태를 반환한다.
     */

    @Transactional(readOnly = true)
    public List<ScheduleResponse.NotAssigned> getMatchedGolfFieldSchedule(Long golfFieldId, Integer year, Integer month) {
        List<Schedule> scheduleList = scheduleRepository.findAllByGolfFieldAndDate(golfFieldId, year, month);

        Map<LocalDate, List<Assignment>> assignmentMap = new HashMap<>();
        Map<LocalDate, Integer> countMap = new HashMap<>();
        for (Schedule schedule : scheduleList) {
            LocalDate date = schedule.getReservationAt();
            assignmentMap.putIfAbsent(date, new ArrayList<>());
            //TODO: assignment의 status가 ASSIGN_REQUESTED 인 경우만 포함하도록 수정
            assignmentMap.get(date).addAll(schedule.getAssignments());
            countMap.put(date, countMap.getOrDefault(date, 0) + schedule.getNotAssignedCnt());
        }

        return assignmentMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> ScheduleResponse.NotAssigned.of(entry.getKey(), countMap.get(entry.getKey()), entry.getValue()))
                .toList();
    }
}
