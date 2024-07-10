package com.flash21.caddycom.entity.reservationSheet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private ReservationSheet reservationSheet;

    @Column(nullable = false)
    private LocalDate reservationAt;

    @Column(nullable = false)
    private Boolean isAssigned;

    @Column(nullable = false)
    private Integer totalCnt;

    @Column(nullable = false)
    private Integer blockedCnt;

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public void setIsAssigned() {
        this.isAssigned = true;
    }
    public void setBlockedCnt(int blockedCnt) {
        this.blockedCnt = blockedCnt;
    }
}
