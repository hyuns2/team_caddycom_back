package com.flash21.caddycom.service.caddy.houseCaddy;

import com.flash21.caddycom.dto.caddy.CaddySearchCond;
import com.flash21.caddycom.dto.caddy.HouseCaddyDto;
import com.flash21.caddycom.dto.caddy.HouseCaddyRequest;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponse;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.global.exception.cException.CCaddyNotFoundException;
import com.flash21.caddycom.global.exception.cException.CTeamNameNotFoundException;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseCaddyService {
    final HouseCaddyRepository houseCaddyRepository;
    public List<String> getHouseCaddyTeam(Long golfFieldId) {
        return houseCaddyRepository.findAllTeam(golfFieldId);
    }

    public List<HouseCaddyDto.houseCaddyResponse> getHouseCaddyByTeam(Long golfFieldId, String teamName) {
        List<HouseCaddy> houseCaddyList = houseCaddyRepository.findAllByTeamAndGolfFieldId(golfFieldId, teamName);
        if (houseCaddyList.isEmpty())
            throw new CTeamNameNotFoundException();

        return houseCaddyList.stream().map(hc -> { return HouseCaddyDto.houseCaddyResponse.builder()
                .id(hc.getId())
                .name(hc.getName())
                .phoneNumber(hc.getPhoneNumber())
                .team(hc.getTeam())
                .teamRole(hc.getTeamRole())
                .holiday(hc.getHoliday())
                .changedHoliday(hc.getChangedHoliday())
                .gender(hc.getGender())
                .birth(hc.getBirth())
                .address(hc.getAddress())
                .addressDetail(hc.getAddressDetail())
                .career(hc.getCareer())
                .build(); } ).toList();
    }

    @Transactional
    public void updateHouseCaddy(Long golfFieldId, Long caddyId, HouseCaddyDto.updateHouseCaddyRequest dto) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(CCaddyNotFoundException::new);

        if (dto.getTeamRole() != null && dto.getTeamRole().equals(TeamRole.LEADER)) {
            houseCaddyRepository.findByTeamAndTeamRoleAndGolFieldId(golfFieldId, houseCaddy.getTeam(), TeamRole.LEADER)
                            .ifPresent((caddy) -> { caddy.setTeamRole(TeamRole.MEMBER); });
        }
        houseCaddy.updateHouseCaddy(dto);
    }

    @Transactional
    public void updateHouseCaddyHoliday(Long caddyId) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(CCaddyNotFoundException::new);

        houseCaddy.updateHoliday();
    }

    public List<HouseCaddyResponse> getAllHouseCaddy(Long golfFieldId, CaddySearchCond searchCond) {

        List<HouseCaddyResponse> findCaddies = houseCaddyRepository.findAllByGoldFieldIdAndSort(
                golfFieldId, searchCond);


        return findCaddies;
    }

    @Transactional(readOnly = true)
    public List<HouseCaddyResponse.TeamHoliday> getAllHoliday(Long golfFieldId) {
        List<HouseCaddy> houseCaddies = houseCaddyRepository.findAllByGolfFieldId(golfFieldId);

        Map<String, List<HouseCaddy>> collect = houseCaddies.stream().collect(Collectors.groupingBy(HouseCaddy::getTeam));

        List<HouseCaddyResponse.TeamHoliday> allHolidays = new ArrayList<>();
        for(String team : collect.keySet()) {
            List<HouseCaddyDto.HolidayInfo> infos = new ArrayList<>();
            for(HouseCaddy houseCaddy : collect.get(team)) {
                HouseCaddyDto.HolidayInfo info = new HouseCaddyDto.HolidayInfo(
                        houseCaddy.getId(),
                        houseCaddy.getName(),
                        houseCaddy.getTeamRole(),
                        houseCaddy.getHoliday());
                infos.add(info);
            }
            allHolidays.add(new HouseCaddyResponse.TeamHoliday(team, infos));
        }

        return allHolidays;
    }

    @Transactional(readOnly = true)
    public HouseCaddyResponse.TeamHoliday getTeamHoliday(Long golfFieldId, String teamName) {
        List<HouseCaddy> caddies = houseCaddyRepository.findAllByTeamAndGolfFieldId(golfFieldId, teamName);

        List<HouseCaddyDto.HolidayInfo> infos = new ArrayList<>();
        for(HouseCaddy houseCaddy : caddies) {
            infos.add(new HouseCaddyDto.HolidayInfo(
                    houseCaddy.getId(),
                    houseCaddy.getName(),
                    houseCaddy.getTeamRole(),
                    houseCaddy.getHoliday()));
        }

        return new HouseCaddyResponse.TeamHoliday(teamName, infos);
    }

    @Transactional
    public void updateHolidayAll(List<HouseCaddyRequest.createHoliday> request) {
        List<Long> ids = request.stream()
                .map(HouseCaddyRequest.createHoliday::getId)
                .toList();

        Map<Long, List<Days>> requestMap = request.stream()
                .collect(Collectors.toMap(HouseCaddyRequest.createHoliday::getId, HouseCaddyRequest.createHoliday::getHolidays
                        , (oldValue, newValue) -> oldValue, HashMap::new));

        List<HouseCaddy> caddies = houseCaddyRepository.findAllByIdIn(ids);

        for(HouseCaddy houseCaddy : caddies) {
            houseCaddy.setHoliday(requestMap.get(houseCaddy.getId()));
        }

    }


    @Transactional
    public void saveCaddyList(List<HouseCaddy> caddyList) {
        //TODO: bulk insert로 변경 필요
        houseCaddyRepository.saveAll(caddyList);
    }
}
