package com.flash21.caddycom.service.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyDto;
import com.flash21.caddycom.dto.caddy.HouseCaddyRequest;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponse;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
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
}
