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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class FreeCaddyService {
    private final FreeCaddyRepository freeCaddyRepository;
    private final GolfFieldRepository golfFieldRepository;
    private final FileUploader fileUploader;

    //TODO: 이미지 업로드 트랜젝션 밖에서 하도록 수정 필요
    //TODO: 지정 골프장 중복 제거
    @Transactional
    public void saveFreeCaddy(FreeCaddyRequest.Create request) {
        FreeCaddy freeCaddy = freeCaddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("앞서 전화번호 인증이 되지 않아 로그인이 제대로 이뤄지지 않았습니다."));

        List<GolfField> golfFieldList = golfFieldRepository.findByIds(request.getGolfFieldIdList());
        if (golfFieldList.size() != request.getGolfFieldIdList().size())
            throw new IllegalArgumentException("존재하지 않는 골프장이 포함되어 있습니다.");

        String profileUrl = request.getProfileUrl() != null
                ? fileUploader.upload(request.getProfileUrl(), "caddy") : null;

        List<MatchedFreeCaddy> matchedFreeCaddyList = golfFieldList.stream()
                .map(golfField -> MatchedFreeCaddy.of(freeCaddy, golfField))
                .toList();

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


    @Transactional(readOnly = true)
    public FreeCaddyResponse.Info getFreeCaddy(Long id) {
        FreeCaddy freeCaddy = freeCaddyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 프리캐디입니다."));
        return FreeCaddyResponse.Info.from(freeCaddy);
    }
}
