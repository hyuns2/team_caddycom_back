package com.flash21.caddycom.service.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponseDto;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.global.common.fileUploader.FileUploader;
import com.flash21.caddycom.global.exception.cException.CCaddyNotFoundException;
import com.flash21.caddycom.global.exception.cException.CInvalidCaddyRequestException;
import com.flash21.caddycom.global.exception.cException.CTeamNameNotFoundException;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseCaddyService {

    private final HouseCaddyRepository houseCaddyRepository;
    private final FileUploader fileUploader;

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
     * 전체 조회: 해당하는 골프장에 속하는 캐디들의 정보를 조별로 반환합니다.
     *
     * @param golfFieldId 골프장 Id
     * @return 조이름, 캐디정보 리스트 맵핑결과
     */
    public Map<String, List<HouseCaddyResponseDto.houseCaddyDetail>> getHouseCaddies(Long golfFieldId) {
        List<HouseCaddy> houseCaddyList = houseCaddyRepository.findAllByGolfFieldIdOrderByTeamAndTeamRole(golfFieldId);

        Map<String, List<HouseCaddyResponseDto.houseCaddyDetail>> result = new TreeMap<>();
        for (HouseCaddy hc: houseCaddyList) {
            if (result.containsKey(hc.getTeam()))
                (result.get(hc.getTeam())).add(toHouseCaddyDetailDto(hc));
            else {
                List<HouseCaddyResponseDto.houseCaddyDetail> dtoList = new ArrayList<>();
                dtoList.add(toHouseCaddyDetailDto(hc));
                result.put(hc.getTeam(), dtoList);
            }
        }
        return result;
    }

    /**
     * 조별 조회: 해당하는 골프장과 조이름에 속하는 캐디들의 정보를 반환합니다.
     *
     * @param golfFieldId 골프장 Id
     * @param teamName    조 이름
     * @return 캐디정보 리스트
     */
    public List<HouseCaddyResponseDto.houseCaddyDetail> getHouseCaddyByTeam(Long golfFieldId, String teamName) {
        String team = teamName.equals("조 없음") ? null : teamName;
        List<HouseCaddy> houseCaddyList = houseCaddyRepository.findAllByGolfFieldIdAndTeam(golfFieldId, team);
        if (houseCaddyList.isEmpty())
            throw new CTeamNameNotFoundException();

        return houseCaddyList.stream().map(this::toHouseCaddyDetailDto).toList();
    }

    private HouseCaddyResponseDto.houseCaddyDetail toHouseCaddyDetailDto(HouseCaddy hc) {
        return HouseCaddyResponseDto.houseCaddyDetail.builder()
                .id(hc.getId())
                .golfFieldName(null)
                .profileUrl(hc.getProfileUrl())
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
    }

    /**
     * 하우스캐디 정보 수정: 관리자가 하우스캐디의 정보를 수정합니다.
     *
     * @param golfFieldId 골프장 Id
     * @param caddyId     캐디 Id
     * @param dto         수정할 정보
     */
    @Transactional
    public void updateHouseCaddyByManager(Long golfFieldId, Long caddyId, HouseCaddyRequestDto.updateHouseCaddyByManager dto) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(CCaddyNotFoundException::new);

        if (dto.getTeamRole() != null && dto.getTeamRole().equals(TeamRole.LEADER)) {
            houseCaddyRepository.findByGolfFieldIdAndTeamAndTeamRole(golfFieldId, houseCaddy.getTeam(), TeamRole.LEADER)
                    .ifPresent((caddy) -> {
                        caddy.setTeamRole(TeamRole.MEMBER);
                    });
        }
        houseCaddy.updateHouseCaddyByManager(dto);
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
                houseCaddyRepository.findAllByGolfFieldIdAndSearchCond(golfFieldId, searchCond)
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

    /**
     * 골프장 내 모든 하우스캐디의 휴무일을 반환합니다.
     *
     * 1. 골프장 id로 하우스캐디 리스트를 조회
     * 2. HouseCaddy 를 조 이름을 기준으로 그룹핑(team필드가 null 인 경우 '조 없음') -> HolidayInfo 로 변환
     * 3. Map<팀 이름, HolidayInfo 리스트> 를 TeamHoliday 로 변환후 팀 이름순으로 정렬
     *
     * @return 조 이름과 하우스캐디 정보(하우스캐디의 id, 이름, 역할, 휴무일) 리스트로 이루어진 DTO 리스트
     */
    public List<HouseCaddyResponseDto.TeamHoliday> getAllHoliday(Long golfFieldId) {
        List<HouseCaddy> houseCaddies = houseCaddyRepository.findAllByGolfFieldId(golfFieldId);

        Map<String, List<HouseCaddyResponseDto.HolidayInfo>> holidayInfoMap = houseCaddies.stream()
                .collect(Collectors.groupingBy(
                        caddy -> caddy.getTeam() != null ? caddy.getTeam() : "조 없음",
                        Collectors.mapping(HouseCaddyResponseDto.HolidayInfo::from, Collectors.toList())
                ));

        List<HouseCaddyResponseDto.TeamHoliday> allHolidays = holidayInfoMap.entrySet().stream()
                .map(entry -> HouseCaddyResponseDto.TeamHoliday.from(entry.getKey(), entry.getValue())) // TeamHoliday 로 변환
                .sorted(Comparator.comparing(HouseCaddyResponseDto.TeamHoliday::getTeam)) // 조 이름 순으로 정렬
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
    public HouseCaddyResponseDto.TeamHoliday getTeamHoliday(Long golfFieldId, String teamName) {
        String team = teamName.equals("조 없음") ? null : teamName;
        List<HouseCaddy> caddies = houseCaddyRepository.findAllByGolfFieldIdAndTeam(golfFieldId, team);

        List<HouseCaddyResponseDto.HolidayInfo> infos = caddies.stream()
                .map(HouseCaddyResponseDto.HolidayInfo::from)
                .collect(Collectors.toList());
        return HouseCaddyResponseDto.TeamHoliday.from(teamName, infos);
    }

    /**
     * 하우스캐디의 휴무일을 수정합니다.
     *
     * @param request 휴무일 수정 요청 DTO
     */
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
            if (team == null) return Integer.MAX_VALUE;
            String numericPart = team.replaceAll("\\D+", "");
            return numericPart.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 하우스캐디 단일 정보조회: 하우스캐디의 정보를 조회합니다
     *
     * @param caddyId 캐디 Id
     * @return 하우스캐디 정보 dto
     */
    public HouseCaddyResponseDto.houseCaddyDetail getHouseCaddy(Long caddyId) {
        HouseCaddy hc = houseCaddyRepository.findById(caddyId)
                .orElseThrow(CCaddyNotFoundException::new);

        return HouseCaddyResponseDto.houseCaddyDetail.builder()
                .id(hc.getId())
                .golfFieldName(hc.getGolfField().getName())
                .profileUrl(hc.getProfileUrl())
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
    }

    /**
     * 하우스캐디 정보 변경: 하우스캐디가 자신의 정보를 변경합니다.
     *
     * @param caddyId 캐디 Id
     * @param dto     변경할 정보 dto
     */
    @Transactional
    public void updateHouseCaddy(Long caddyId, HouseCaddyRequestDto.updateHouseCaddy dto) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(CCaddyNotFoundException::new);

        String profileUrl = dto.getProfile() != null ? fileUploader.upload(dto.getProfile(), "/caddy") : null;
        houseCaddy.update(profileUrl,
                dto.getChangedHoliday(),
                dto.getBirth(),
                dto.getAddress(),
                dto.getAddressDetail(),
                dto.getCareer());
    }
}
