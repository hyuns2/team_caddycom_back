package com.flash21.caddycom.service;

import com.flash21.caddycom.repository.ReservationSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationSheetService {
    final ReservationSheetRepository rsRepository;

}
