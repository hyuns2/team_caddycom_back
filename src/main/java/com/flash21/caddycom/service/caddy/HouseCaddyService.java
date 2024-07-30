package com.flash21.caddycom.service.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyDto;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponse;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseCaddyService {
    private final HouseCaddyRepository houseCaddyRepository;

    @Transactional(readOnly = true)
    public List<HouseCaddyResponse.TeamHoliday> getAllHoliday(Long golfFieldId) {
        List<HouseCaddy> houseCaddies = houseCaddyRepository.findAllByGolfFieldId(golfFieldId)
                .orElseThrow(() -> new NoSuchElementException("골프장에 등록된 하우스 캐디가 없습니다."));

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

}
