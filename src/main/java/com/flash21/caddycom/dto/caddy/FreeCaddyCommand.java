package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.flash21.caddycom.global.util.Formatter.formatPhoneNumber;

public class FreeCaddyCommand {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Create {
        private String name;
        private String phoneNumber;
        private String regions;
        private Gender gender;
        private LocalDate birth;
        private String career;
        private String intro;
        private List<Long> golfFieldIds;
        private MultipartFile profileUrl;

        public static FreeCaddyCommand.Create from(FreeCaddyRequest.Create request) {
            return Create.builder()
                    .name(request.getName())
                    .phoneNumber(formatPhoneNumber(request.getPhoneNumber()))
                    .regions(request.getRegions())
                    .gender(request.getGender())
                    .birth(request.getBirth())
                    .career(request.getCareer())
                    .intro(request.getIntro())
                    .golfFieldIds(request.getGolfFieldIds())
                    .profileUrl(request.getProfileUrl())
                    .build();
        }
    }
}
