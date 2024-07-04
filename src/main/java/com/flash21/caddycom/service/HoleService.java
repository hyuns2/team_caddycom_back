package com.flash21.caddycom.service;

import com.flash21.caddycom.dto.hole.HandicapUpdate;
import com.flash21.caddycom.entity.Hole;
import com.flash21.caddycom.repository.HoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HoleService {
    private final HoleRepository holeRepository;

    @Transactional
    public void updateHandicap(HandicapUpdate request) {
        Hole hole = holeRepository.findById(request.getHoleId())
                .orElseThrow(() -> new IllegalArgumentException("해당 홀이 존재하지 않습니다."));

        hole.updateHandicap(request.getHandicap());
    }
}
