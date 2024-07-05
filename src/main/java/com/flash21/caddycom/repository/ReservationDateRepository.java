package com.flash21.caddycom.repository;

import com.flash21.caddycom.dto.ReservationSheetDto;
import com.flash21.caddycom.entity.ReservationDate;
import com.flash21.caddycom.entity.ReservationSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationDateRepository extends JpaRepository<ReservationDate, Long> {
    @Query("select d.reservationAt as reservationAt, sum(d.totalCnt) as totalCntSum, sum(d.blockedCnt) as blockedCntSum from ReservationDate d left join d.reservationSheet"
            + " where d.reservationAt between ?1 and ?2"
            + " and d.reservationSheet.id in ?3 group by d.reservationAt order by d.reservationAt")
    List<MetaDataReport> countAllMetaDataByDate(LocalDate startDate, LocalDate endDate, List<Long> reservationSheetList);
}
