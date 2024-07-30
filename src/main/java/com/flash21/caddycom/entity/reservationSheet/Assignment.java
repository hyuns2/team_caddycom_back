package com.flash21.caddycom.entity.reservationSheet;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.schedule.Schedule;
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
    private Schedule schedule;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private AssignmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private HouseCaddy houseCaddy;

    private String caddyName;

    private String reason;
}
