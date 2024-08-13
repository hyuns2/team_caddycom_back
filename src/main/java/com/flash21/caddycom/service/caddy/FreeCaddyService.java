package com.flash21.caddycom.service.caddy;

import com.flash21.caddycom.dto.caddy.FreeCaddyRequest;
import com.flash21.caddycom.dto.caddy.FreeCaddyResponse;
import com.flash21.caddycom.entity.caddy.FreeCaddy;
import com.flash21.caddycom.entity.caddy.MatchedFreeCaddy;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.global.common.fileUploader.FileUploader;
import com.flash21.caddycom.repository.caddy.FreeCaddyRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FreeCaddyService {
    private final FreeCaddyRepository freeCaddyRepository;
    private final GolfFieldRepository golfFieldRepository;
    private final FileUploader fileUploader;
    private final ApplicationContext applicationContext;


    /**
     * 1. 전화번호로 FreeCaddy를 찾는다.
     * 2. 프로필 이미지를 업로드 한다.
     * 3. 요청된 지정 골프장 중 추가될 것만 추출한다.
     * 4. 요청값으로 FreeCaddy를 저장한다.
     */
    public void saveFreeCaddy(FreeCaddyRequest.Create request) {
        FreeCaddy freeCaddy = freeCaddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("앞서 전화번호 인증이 되지 않아 로그인이 제대로 이뤄지지 않았습니다."));

        String profileUrl = getProfileUrl(request);
        List<MatchedFreeCaddy> matchedFreeCaddyList = getDistinctGolfField(freeCaddy, request.getGolfFieldIds());

        // transaction self invocation 을 피하기 위해 applicationContext를 통해 자신의 메소드를 이용한다.
        FreeCaddyService self = applicationContext.getBean(FreeCaddyService.class);
        self.saveEntity(request, freeCaddy, matchedFreeCaddyList, profileUrl);
    }


    @Transactional
    protected void saveEntity(FreeCaddyRequest.Create request,
                              FreeCaddy freeCaddy,
                              List<MatchedFreeCaddy> matchedFreeCaddyList,
                              String profileUrl) {
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
     * 프로필 이미지를 업로드하고 업로드된 이미지 URL을 반환한다. (없으면 null을 반환)
     */
    private String getProfileUrl(FreeCaddyRequest.Create request) {
        return request.getProfileUrl() != null
                ? fileUploader.upload(request.getProfileUrl(), "caddy") : null;
    }


    /**
     * 1. 요청된 지정 골프장이 1개 이상인지 확인한다.
     * 2. 요청된 지정 골프장 중 존재하지 않는것이 있는지 확인한다.
     * 3. 요청된 것중 이미 매칭된 골프장은 제외한다.
     * 4. MatchedFreeCaddy 목록을 반환한다.
     */
    protected List<MatchedFreeCaddy> getDistinctGolfField(FreeCaddy freeCaddy, List<Long> golfFieldIds) {
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


    @Transactional(readOnly = true)
    public FreeCaddyResponse.Info getFreeCaddy(Long id) {
        FreeCaddy freeCaddy = freeCaddyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 프리캐디입니다."));
        return FreeCaddyResponse.Info.from(freeCaddy);
    }
}
