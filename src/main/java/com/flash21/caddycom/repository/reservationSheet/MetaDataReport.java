package com.flash21.caddycom.repository.reservationSheet;

import java.time.LocalDate;

public interface MetaDataReport {
    LocalDate getReservationAt();
    Integer getTotalCntSum();
    Integer getBlockedCntSum();
}
