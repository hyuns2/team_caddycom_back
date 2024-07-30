package com.flash21.caddycom.entity.schedule;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private GolfField golfField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Course course;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String teeOff;

    @Column(nullable = false)
    private Integer part;

    @Column(nullable = false)
    private Boolean isAssigned;

    @Column(nullable = false)
    private Integer totalCnt;

    @Column(nullable = false)
    private Integer blockedCnt;
}
