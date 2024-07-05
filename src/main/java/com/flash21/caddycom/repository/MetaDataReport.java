package com.flash21.caddycom.repository;

import java.time.LocalDate;

public interface MetaDataReport {
    LocalDate getReservationAt();
    Integer getTotalCntSum();
    Integer getBlockedCntSum();
}
