package com.flash21.caddycom.service.caddy;

import com.flash21.caddycom.dto.caddy.FreeCaddyCommand;
import com.flash21.caddycom.entity.caddy.FreeCaddy;
import com.flash21.caddycom.entity.caddy.MatchedFreeCaddy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FreeCaddyContentService {
    @Transactional
    protected void saveEntity(FreeCaddyCommand.Create request,
                              FreeCaddy freeCaddy,
                              List<MatchedFreeCaddy> matchedFreeCaddyList,
                              String profileUrl)
    {
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
}
