package com.flash21.caddycom.repository.reservationSheet;

import com.flash21.caddycom.entity.schedule.DateStatus;

import java.time.LocalDate;

public interface MetaDataReport {
    LocalDate getReservationAt();
    DateStatus getDateStatus();
    Integer getTotalCntSum();
    Integer getBlockedCntSum();
}
