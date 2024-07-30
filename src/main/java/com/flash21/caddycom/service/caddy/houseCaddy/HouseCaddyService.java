package com.flash21.caddycom.service.caddy.houseCaddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyDto;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.global.exception.cException.CCaddyNotFoundException;
import com.flash21.caddycom.global.exception.cException.CTeamNameNotFoundException;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HouseCaddyService {
    final HouseCaddyRepository houseCaddyRepository;
    public List<String> getHouseCaddyTeam(Long golfFieldId) {
        return houseCaddyRepository.findAllTeam(golfFieldId);
    }

    public List<HouseCaddyDto.houseCaddyResponse> getHouseCaddyByTeam(String teamName) {
        List<HouseCaddy> houseCaddyList = houseCaddyRepository.findAllByTeam(teamName);
        if (houseCaddyList.isEmpty())
            throw new CTeamNameNotFoundException();

        return houseCaddyList.stream().map(hc -> { return HouseCaddyDto.houseCaddyResponse.builder()
                .id(hc.getId())
                .name(hc.getName())
                .phoneNumber(hc.getPhoneNumber())
                .team(hc.getTeam())
                .teamRole(hc.getTeamRole())
                .holiday(hc.getHoliday())
                .changeHoliday(hc.getHoliday())
                .gender(hc.getGender())
                .birth(hc.getBirth())
                .address(hc.getAddress())
                .addressDetail(hc.getAddressDetail())
                .career(hc.getCareer())
                .build(); } ).toList();
    }

    @Transactional
    public void updateHouseCaddy(Long caddyId, HouseCaddyDto.updateHouseCaddyRequest dto) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(CCaddyNotFoundException::new);

        if (dto.getTeamRole() != null && dto.getTeamRole().equals(TeamRole.LEADER)) {
            houseCaddyRepository.findByTeamAndTeamRole(dto.getTeam(), TeamRole.LEADER)
                            .ifPresent((caddy) -> { caddy.setTeamRole(TeamRole.MEMBER); });
        }
        houseCaddy.updateHouseCaddy(dto);
    }
}
