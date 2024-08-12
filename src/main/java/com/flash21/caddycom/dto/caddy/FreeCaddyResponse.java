package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.FreeCaddy;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.golfField.GolfField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class FreeCaddyResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info {
        private String name;
        private String profileUrl;
        private Long point;
        private String regions;
        private Gender gender;
        private LocalDate birth;
        private String career;
        private String intro;
        private List<MatchedGolfFieldInfo> golfFieldList;

        public static Info from(FreeCaddy freeCaddy) {
            return Info.builder()
                    .name(freeCaddy.getName())
                    .profileUrl(freeCaddy.getProfileUrl())
                    .point(freeCaddy.getPoint())
                    .regions(freeCaddy.getRegions())
                    .gender(freeCaddy.getGender())
                    .birth(freeCaddy.getBirth())
                    .career(freeCaddy.getCareer())
                    .intro(freeCaddy.getIntro())
                    .golfFieldList(freeCaddy.getMatchedFreeCaddyList().stream()
                            .map(matchedFreeCaddy -> MatchedGolfFieldInfo.from(matchedFreeCaddy.getGolfField()))
                            .toList())
                    .build();

        }
    }


    //TODO: 상세정보 구체화되면 추가 필요
    @Getter
    @Builder
    @AllArgsConstructor
    private static class MatchedGolfFieldInfo {
        private String name;
        private String imageUrl;

        public static MatchedGolfFieldInfo from(GolfField golfField) {
            return MatchedGolfFieldInfo.builder()
                    .name(golfField.getName())
                    .imageUrl(golfField.getImageUrl())
                    .build();
        }
    }
}
