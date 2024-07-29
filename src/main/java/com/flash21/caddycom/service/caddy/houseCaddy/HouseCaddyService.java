package com.flash21.caddycom.service.caddy.houseCaddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyDto;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseCaddyService {
    final HouseCaddyRepository houseCaddyRepository;
    public List<String> getHouseCaddyTeam(Long golfFieldId) {
        return houseCaddyRepository.findAllTeam(golfFieldId);
    }

    public List<HouseCaddyDto.houseCaddyResponse> getHouseCaddyByTeam(String teamName) {
        List<HouseCaddy> houseCaddyList = houseCaddyRepository.findAllByTeam(teamName);

        return houseCaddyList.stream().map(hc -> { return HouseCaddyDto.houseCaddyResponse.builder()
                .id(hc.getId())
                .name(hc.getName())
                .phoneNumber(hc.getPhoneNumber())
                .team(hc.getTeam())
                .role(hc.getTeamRole())
                .holiday(hc.getHoliday())
                .changeHoliday(hc.getHoliday())
                .gender(hc.getGender())
                .birth(hc.getBirth())
                .address(hc.getAddress())
                .addressDetail(hc.getAddressDetail())
                .career(hc.getCareer())
                .build(); } ).toList();
    }
}
