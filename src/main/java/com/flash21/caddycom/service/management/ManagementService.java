package com.flash21.caddycom.service.management;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ManagementService {
    private final GolfFieldRepository golfFieldRepository;
    @Transactional
    public void approveRegistration(Long id){
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        golfField.approve();
    }
}
