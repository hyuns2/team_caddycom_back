package com.flash21.caddycom.service.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequest;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponse;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.global.exception.cException.CCaddyNotFoundException;
import com.flash21.caddycom.global.exception.cException.CInvalidCaddyRequestException;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseCaddyHolidayService {
    private final HouseCaddyRepository houseCaddyRepository;

    /**
     * 하우스캐디 휴무일 승인: 하우스캐디가 요청한 휴무일로 변경합니다.
     */
    @Transactional
    public void updateHouseCaddyHoliday(Long caddyId) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(CCaddyNotFoundException::new);

        if (houseCaddy.getChangedHoliday() == null)
            throw new CInvalidCaddyRequestException();

        houseCaddy.updateHoliday();
    }

    /**
     * 골프장 내 모든 하우스캐디의 휴무일을 반환합니다.
     *
     * 1. 골프장 id로 하우스캐디 리스트를 조회
     * 2. HouseCaddy 를 조 이름을 기준으로 그룹핑(team필드가 null 인 경우 '조 없음') -> HolidayInfo 로 변환
     * 3. Map<팀 이름, HolidayInfo 리스트> 를 TeamHoliday 로 변환후 팀 이름순으로 정렬
     *
     * @return 조 이름과 하우스캐디 정보(하우스캐디의 id, 이름, 역할, 휴무일) 리스트로 이루어진 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<HouseCaddyResponse.TeamHoliday> getAllHoliday(Long golfFieldId) {
        List<HouseCaddy> houseCaddies = houseCaddyRepository.findAllByGolfFieldId(golfFieldId);

        Map<String, List<HouseCaddyResponse.HolidayInfo>> holidayInfoMap = houseCaddies.stream()
                .collect(Collectors.groupingBy(
                        caddy -> caddy.getTeam() != null ? caddy.getTeam() : "조 없음",
                        Collectors.mapping(HouseCaddyResponse.HolidayInfo::from, Collectors.toList())
                ));

        List<HouseCaddyResponse.TeamHoliday> allHolidays = holidayInfoMap.entrySet().stream()
                .map(entry -> HouseCaddyResponse.TeamHoliday.from(entry.getKey(), entry.getValue())) // TeamHoliday 로 변환
                .sorted(Comparator.comparing(HouseCaddyResponse.TeamHoliday::getTeam)) // 조 이름 순으로 정렬
                .collect(Collectors.toList());

        return allHolidays;
    }

    /**
     * 특정 조에 속하는 하우스캐디의 휴무일을 반환합니다.
     * '조 없음'이 입력으로 들어올 경우 team이 없는 캐디를 조회 (IS NULL)
     *
     * @param golfFieldId 골프장 id
     * @param teamName    조 이름
     * @return 조 이름과 하우스캐디 정보 리스트(하우스캐디의 id, 이름, 역할, 휴무일) 로 이루어진 DTO
     */
    @Transactional(readOnly = true)
    public HouseCaddyResponse.TeamHoliday getTeamHoliday(Long golfFieldId, String teamName) {
        String team = teamName.equals("조 없음") ? null : teamName;
        List<HouseCaddy> caddies = houseCaddyRepository.findAllByGolfFieldIdAndTeam(golfFieldId, team);

        List<HouseCaddyResponse.HolidayInfo> infos = caddies.stream()
                .map(HouseCaddyResponse.HolidayInfo::from)
                .collect(Collectors.toList());
        return HouseCaddyResponse.TeamHoliday.from(teamName, infos);
    }

    /**
     * 하우스캐디의 휴무일을 수정합니다.
     *
     * @param request 휴무일 수정 요청 DTO
     */
    @Transactional
    public void updateHolidayAll(List<HouseCaddyRequest.CreateHoliday> request) {
        List<Long> ids = request.stream()
                .map(HouseCaddyRequest.CreateHoliday::getId)
                .toList();

        Map<Long, List<Days>> requestMap = request.stream()
                .collect(Collectors.toMap(HouseCaddyRequest.CreateHoliday::getId, HouseCaddyRequest.CreateHoliday::getHolidays
                        , (oldValue, newValue) -> oldValue, HashMap::new));

        List<HouseCaddy> caddies = houseCaddyRepository.findAllByIdIn(ids);

        for (HouseCaddy houseCaddy : caddies) {
            houseCaddy.setHoliday(requestMap.get(houseCaddy.getId()));
        }

    }
}
