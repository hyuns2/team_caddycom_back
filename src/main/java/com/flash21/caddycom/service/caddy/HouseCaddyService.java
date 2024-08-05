package com.flash21.caddycom.service.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponseDto;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.global.exception.cException.CCaddyNotFoundException;
import com.flash21.caddycom.global.exception.cException.CInvalidCaddyRequestException;
import com.flash21.caddycom.global.exception.cException.CTeamNameNotFoundException;
import com.flash21.caddycom.repository.caddy.HouseCaddyQueryFactory;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HouseCaddyService {

    private final HouseCaddyRepository houseCaddyRepository;
    private final HouseCaddyQueryFactory houseCaddyQueryFactory;

    /**
     * 조 전제조회: 골프장 Id에 해당하는 캐디의 조이름을 전부 반환합니다.
     *
     * @param golfFieldId 골프장 Id
     * @return 조이름 리스트
     */
    public List<String> getHouseCaddyTeam(Long golfFieldId) {
        return houseCaddyRepository.findAllTeam(golfFieldId);
    }

    /**
     * 조별 조회: 해당하는 골프장과 조이름에 속하는 캐디들의 정보를 반환합니다.
     *
     * @param golfFieldId 골프장 Id
     * @param teamName    조 이름
     * @return 캐디정보 리스트
     */
    public List<HouseCaddyResponseDto.houseCaddyDetail> getHouseCaddyByTeam(Long golfFieldId, String teamName) {
        List<HouseCaddy> houseCaddyList = houseCaddyRepository.findAllByGolfFieldIdAndTeam(golfFieldId, teamName);
        if (houseCaddyList.isEmpty())
            throw new CTeamNameNotFoundException();

        return houseCaddyList.stream().map(hc -> {
            return HouseCaddyResponseDto.houseCaddyDetail.builder()
                    .id(hc.getId())
                    .name(hc.getName())
                    .phoneNumber(hc.getPhoneNumber())
                    .team(hc.getTeam())
                    .teamRole(hc.getTeamRole())
                    .holiday(hc.getHoliday())
                    .changedHoliday(hc.getChangedHoliday())
                    .offPart(hc.getOffPart())
                    .gender(hc.getGender())
                    .birth(hc.getBirth())
                    .address(hc.getAddress())
                    .addressDetail(hc.getAddressDetail())
                    .career(hc.getCareer())
                    .caddyType(hc.getCaddyType())
                    .build();
        }).toList();
    }

    /**
     * 하우스캐디 정보 수정: 하우스캐디의 정보를 수정합니다.
     *
     * @param golfFieldId 골프장 Id
     * @param caddyId     캐디 Id
     * @param dto         수정할 정보
     */
    @Transactional
    public void updateHouseCaddy(Long golfFieldId, Long caddyId, HouseCaddyRequestDto.updateHouseCaddy dto) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(CCaddyNotFoundException::new);

        if (dto.getTeamRole() != null && dto.getTeamRole().equals(TeamRole.LEADER)) {
            houseCaddyRepository.findByGolfFieldIdAndTeamAndTeamRole(golfFieldId, houseCaddy.getTeam(), TeamRole.LEADER)
                    .ifPresent((caddy) -> {
                        caddy.setTeamRole(TeamRole.MEMBER);
                    });
        }
        houseCaddy.updateHouseCaddy(dto);
    }

    /**
     * 하우스캐디 휴무일 승인: 하우스캐디가 요청한 휴무일로 변경합니다.
     *
     * @param caddyId 캐디 Id
     */
    @Transactional
    public void updateHouseCaddyHoliday(Long caddyId) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(CCaddyNotFoundException::new);

        if (houseCaddy.getChangedHoliday() == null)
            throw new CInvalidCaddyRequestException();

        houseCaddy.updateHoliday();
    }

    public Map<String, List<HouseCaddyResponseDto.Info>> getAllHouseCaddy(Long golfFieldId, HouseCaddyRequestDto.CaddySearchCond searchCond) {

        List<HouseCaddyResponseDto.Info> findHouseCaddies =
                houseCaddyQueryFactory.findAllByGolfFieldIdAndSearchCond(golfFieldId, searchCond)
                        .stream()
                        .map(HouseCaddyResponseDto.Info::new)
                        .sorted(this::comparing)
                        .toList();

        return findHouseCaddies.stream()
                .collect(Collectors.groupingBy(
                        HouseCaddyResponseDto.Info::getTeam,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    @Transactional(readOnly = true)
    public List<HouseCaddyResponseDto.TeamHoliday> getAllHoliday(Long golfFieldId) {
        List<HouseCaddy> houseCaddies = houseCaddyRepository.findAllByGolfFieldId(golfFieldId);

        Map<String, List<HouseCaddy>> collect = houseCaddies.stream().collect(Collectors.groupingBy(HouseCaddy::getTeam));

        List<HouseCaddyResponseDto.TeamHoliday> allHolidays = new ArrayList<>();
        for (String team : collect.keySet()) {
            List<HouseCaddyResponseDto.HolidayInfo> infos = new ArrayList<>();
            for (HouseCaddy houseCaddy : collect.get(team)) {
                HouseCaddyResponseDto.HolidayInfo info = new HouseCaddyResponseDto.HolidayInfo(
                        houseCaddy.getId(),
                        houseCaddy.getName(),
                        houseCaddy.getTeamRole(),
                        houseCaddy.getHoliday());
                infos.add(info);
            }
            allHolidays.add(new HouseCaddyResponseDto.TeamHoliday(team, infos));
        }

        return allHolidays;
    }

    @Transactional(readOnly = true)
    public HouseCaddyResponseDto.TeamHoliday getTeamHoliday(Long golfFieldId, String teamName) {
        List<HouseCaddy> caddies = houseCaddyRepository.findAllByGolfFieldIdAndTeam(golfFieldId, teamName);

        List<HouseCaddyResponseDto.HolidayInfo> infos = new ArrayList<>();
        for (HouseCaddy houseCaddy : caddies) {
            infos.add(new HouseCaddyResponseDto.HolidayInfo(
                    houseCaddy.getId(),
                    houseCaddy.getName(),
                    houseCaddy.getTeamRole(),
                    houseCaddy.getHoliday()));
        }

        return new HouseCaddyResponseDto.TeamHoliday(teamName, infos);
    }

    @Transactional
    public void updateHolidayAll(List<HouseCaddyRequestDto.createHoliday> request) {
        List<Long> ids = request.stream()
                .map(HouseCaddyRequestDto.createHoliday::getId)
                .toList();

        Map<Long, List<Days>> requestMap = request.stream()
                .collect(Collectors.toMap(HouseCaddyRequestDto.createHoliday::getId, HouseCaddyRequestDto.createHoliday::getHolidays
                        , (oldValue, newValue) -> oldValue, HashMap::new));

        List<HouseCaddy> caddies = houseCaddyRepository.findAllByIdIn(ids);

        for (HouseCaddy houseCaddy : caddies) {
            houseCaddy.setHoliday(requestMap.get(houseCaddy.getId()));
        }

    }

    @Transactional
    public void saveCaddyList(Long golfFieldId, List<HouseCaddy> caddyList) {
        //TODO: bulk insert로 변경 필요
        houseCaddyRepository.bulkInsert(caddyList, golfFieldId);
    }


    private int comparing(HouseCaddyResponseDto.Info hc1, HouseCaddyResponseDto.Info hc2) {
        int teamNumber1 = extractTeamNumber(hc1.getTeam());
        int teamNumber2 = extractTeamNumber(hc2.getTeam());
        if (teamNumber1 != teamNumber2) {
            return Integer.compare(teamNumber1, teamNumber2);
        }
        if (hc1.getTeamRole() == TeamRole.LEADER && hc2.getTeamRole() != TeamRole.LEADER) {
            return -1;
        } else if (hc1.getTeamRole() != TeamRole.LEADER && hc2.getTeamRole() == TeamRole.LEADER) {
            return 1;
        }
        return hc1.getName().compareTo(hc2.getName());
    }

    private int extractTeamNumber(String team) {
        try {
            String numericPart = team.replaceAll("\\D+", "");
            return numericPart.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}
