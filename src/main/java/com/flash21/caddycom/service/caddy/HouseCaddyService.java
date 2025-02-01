package com.flash21.caddycom.service.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequest;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponse;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.global.common.fileReader.EntityConverter;
import com.flash21.caddycom.global.common.fileReader.ExcelReader;
import com.flash21.caddycom.global.common.fileUploader.FileUploader;
import com.flash21.caddycom.global.exception.CustomException;
import com.flash21.caddycom.global.exception.ErrorCode;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HouseCaddyService {

    private final HouseCaddyRepository houseCaddyRepository;
    private final GolfFieldRepository golfFieldRepository;
    private final FileUploader fileUploader;
    private final EntityConverter entityConverter;
    private final ExcelReader excelReader;
    private final PlatformTransactionManager transactionManager;

    /**
     * 조 전체조회: 골프장 Id에 해당하는 캐디의 조이름을 전부 반환합니다.
     *
     * @return 골프장 Id에 해당하는 조이름 리스트
     */
    @Transactional(readOnly = true)
    public List<String> getHouseCaddyTeam(Long golfFieldId) {
        return houseCaddyRepository.findAllTeam(golfFieldId);
    }

    /**
     * 조별 전체조회: 해당하는 골프장에 속하는 캐디들의 정보를 조별로 반환합니다.
     *
     * @return 조이름, 캐디정보 리스트 맵핑결과
     */
    @Transactional(readOnly = true)
    public Map<String, List<HouseCaddyResponse.Detail>> getHouseCaddies(Long golfFieldId) {
        List<HouseCaddy> houseCaddyList = houseCaddyRepository.findAllByGolfFieldIdOrderByTeamAndTeamRole(golfFieldId);

        Map<String, List<HouseCaddyResponse.Detail>> result = new TreeMap<>();
        for (HouseCaddy hc: houseCaddyList) {
            if (result.containsKey(hc.getTeam()))
                (result.get(hc.getTeam())).add(HouseCaddyResponse.Detail.from(hc, null));
            else {
                List<HouseCaddyResponse.Detail> dtoList = new ArrayList<>();
                dtoList.add(HouseCaddyResponse.Detail.from(hc,null));
                result.put(hc.getTeam(), dtoList);
            }
        }
        return result;
    }

    /**
     * 조별 조회: 해당하는 골프장과 조이름에 속하는 캐디들의 정보를 반환합니다.
     */
    @Transactional(readOnly = true)
    public List<HouseCaddyResponse.Detail> getHouseCaddyByTeam(Long golfFieldId, String teamName) {
        String team = teamName.equals("조 없음") ? null : teamName;
        List<HouseCaddy> houseCaddyList = houseCaddyRepository.findAllByGolfFieldIdAndTeam(golfFieldId, team);
        if (houseCaddyList.isEmpty())
            throw new CustomException(ErrorCode.TEAM_NAME_NOT_FOUND);

        return houseCaddyList.stream()
                .map(houseCaddy -> HouseCaddyResponse.Detail.from(houseCaddy,null))
                .toList();
    }


    /**
     * 하우스캐디 정보 수정: 관리자가 하우스캐디의 정보를 수정합니다.
     */
    @Transactional
    public void updateHouseCaddyByManager(Long golfFieldId, Long caddyId, HouseCaddyRequest.UpdateByManager dto) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(() -> new CustomException(ErrorCode.CADDY_NOT_FOUND));

        if (dto.getTeamRole() != null && dto.getTeamRole().equals(TeamRole.LEADER)) {
            houseCaddyRepository.findByGolfFieldIdAndTeamAndTeamRole(golfFieldId, houseCaddy.getTeam(), TeamRole.LEADER)
                    .ifPresent((caddy) -> {
                        caddy.setTeamRole(TeamRole.MEMBER);
                    });
        }
        houseCaddy.updateHouseCaddyByManager(dto.getTeam(), dto.getTeamRole(), dto.getHoliday(),
                dto.getOffPart(), dto.getGender(), dto.getBirth(), dto.getAddress(),
                dto.getAddressDetail(), dto.getCareer());
    }

    @Transactional(readOnly = true)
    public Map<String, List<HouseCaddyResponse.Info>> getAllHouseCaddy(Long golfFieldId, HouseCaddyRequest.CaddySearchCond searchCond) {

        List<HouseCaddyResponse.Info> findHouseCaddies =
                houseCaddyRepository.findAllByGolfFieldIdAndSearchCond(golfFieldId, searchCond)
                        .stream()
                        .map(HouseCaddyResponse.Info::new)
                        .sorted(this::comparing)
                        .toList();

        return findHouseCaddies.stream()
                .collect(Collectors.groupingBy(
                        HouseCaddyResponse.Info::getTeam,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }



    private int comparing(HouseCaddyResponse.Info hc1, HouseCaddyResponse.Info hc2) {
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

    /**
     * 팀 이름에서 숫자를 추출한다.
     * 팀 이름이 없으면 최댓값을 부여
     */
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
     */
    @Transactional(readOnly = true)
    public HouseCaddyResponse.Detail getHouseCaddy(Long caddyId) {
        HouseCaddy hc = houseCaddyRepository.findById(caddyId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        return HouseCaddyResponse.Detail.from(hc, hc.getGolfField().getName());
    }

    /**
     * 하우스캐디 정보 변경: 하우스캐디가 자신의 정보를 변경합니다.
     */
    @Transactional
    public void updateHouseCaddy(Long caddyId, HouseCaddyRequest.UpdateByCaddy dto) {
        HouseCaddy houseCaddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        String profileUrl = fileUploader.upload(dto.getProfile(), "caddy/");

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            houseCaddy.update(profileUrl,
                    dto.getChangedHoliday(),
                    dto.getBirth(),
                    dto.getAddress(),
                    dto.getAddressDetail(),
                    dto.getCareer());
            return null;
        });

    }

    /**
     * 엑셀 파일을 읽어 하우스 캐디 일괄 저장
     */
    public void uploadCaddy(Long id, MultipartFile file) {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));

        List<List<String>> stringData = excelReader.readExcelToList(file);
        List<HouseCaddy> caddyList = stringData.stream()
                .map(entityConverter::toEntity)
                .toList();

        houseCaddyRepository.bulkInsert(caddyList, golfField.getId());
    }


}
