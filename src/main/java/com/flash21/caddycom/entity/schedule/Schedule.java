package com.flash21.caddycom.entity.schedule;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private GolfField golfField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationSheet reservationSheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDate reservationAt;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String teeOff;

    @Column(nullable = false)
    private Integer part;

    @Column(nullable = false)
    private DateStatus dateStatus;

    @Column(nullable = false)
    private Integer totalCnt;

    @Column(nullable = false)
    private Integer blockedCnt;

    @Column(nullable = false)
    private Integer notAssignedCnt;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Assignment> assignments;

    public static Schedule of(GolfField golfField, ReservationSheet reservationSheet, Course course,
                              LocalDate reservationAt, LocalTime startTime, LocalTime endTime,
                              String teeOff, int part, int totalCnt) {
        return Schedule.builder()
                .golfField(golfField)
                .reservationSheet(reservationSheet)
                .course(course)
                .reservationAt(reservationAt)
                .startTime(startTime)
                .endTime(endTime)
                .teeOff(teeOff)
                .part(part)
                .dateStatus(DateStatus.NOTHING)
                .totalCnt(totalCnt)
                .notAssignedCnt(0)
                .blockedCnt(0)
                .build();
    }

    public void updateDateStatus(DateStatus dateStatus) {
        this.dateStatus = dateStatus;
    }

    public void addBlockCount() {
        this.blockedCnt++;
    }

    public void subBlockCount() {
        if (this.blockedCnt >= 1) {
            this.blockedCnt--;
        }
    }
    //외부캐디 요청을 했을때 미배정 개수를 증가시키는 메소드
    public void addNotAssignedCount() {
        if (this.notAssignedCnt < this.totalCnt)
            this.notAssignedCnt++;
    }

    public void subNotAssignedCount() {
        if (this.notAssignedCnt >= 1) {
            this.notAssignedCnt--;
        }
    }
}
