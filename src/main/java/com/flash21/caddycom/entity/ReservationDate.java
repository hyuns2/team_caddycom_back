package com.flash21.caddycom.entity;

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
    private Boolean status;

    @Column(nullable = false)
    private Integer totalCnt;

    @Column(nullable = false)
    private Integer blockedCnt;
}
