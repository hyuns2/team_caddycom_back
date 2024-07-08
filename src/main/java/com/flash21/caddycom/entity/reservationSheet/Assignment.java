package com.flash21.caddycom.entity.reservationSheet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    ReservationDate reservationDate;

    @Column(nullable = false)
    LocalTime startTime;

    @Column(nullable = false)
    AssignmentStatus status;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn
//    Caddy caddy;

    @Column
    String caddyName;

    @Column
    String reason;
}
