package com.flash21.caddycom.entity.schedule;

import com.flash21.caddycom.entity.caddy.Caddy;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "assignment")
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
    private AssignmentStatus assignmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Caddy caddy;

    private String caddyName;

    private String reason;

    private LocalTime startedTime;

    private LocalTime endedTime;

    public static Assignment of(Schedule schedule, LocalTime startTime) {
        return Assignment.builder()
                .schedule(schedule)
                .startTime(startTime)
                .assignmentStatus(AssignmentStatus.NOTHING)
                .build();
    }

    public void cancel(String reason) {
        if (reason != null && !reason.isEmpty())
            this.reason = reason;
        this.assignmentStatus = AssignmentStatus.CANCELED;
    }

    public void requestCancel(String reason) {
        if (reason != null && !reason.isEmpty())
            this.reason = reason;
        this.assignmentStatus = AssignmentStatus.CANCEL_REQUESTED;
    }

    public void blockAssignment(String reason) {
        if (this.assignmentStatus != AssignmentStatus.BLOCKED) {
            this.assignmentStatus = AssignmentStatus.BLOCKED;
            this.reason = reason;
            this.schedule.addBlockCount();
        } else if (this.assignmentStatus == AssignmentStatus.BLOCKED) {
            this.reason = reason;
        }
    }

    public void cancelBlock() {
        if (this.assignmentStatus == AssignmentStatus.BLOCKED) {
            this.assignmentStatus = AssignmentStatus.NOTHING;
            this.reason = null;
            this.caddyName = null;
            this.caddy = null;
            this.schedule.subBlockCount();
        }
    }

    public void assignCaddy(Caddy caddy) {
        this.caddyName = caddy.getName();
        this.caddy = caddy;
        caddy.getAssignmentList().add(this);
        if (this.assignmentStatus == AssignmentStatus.BLOCKED) return;
        this.assignmentStatus = AssignmentStatus.ASSIGNED;
        this.schedule.subNotAssignedCount();
    }

    public void requestFreeCaddy() {
        this.assignmentStatus = AssignmentStatus.ASSIGN_REQUESTED;
        this.schedule.addNotAssignedCount();
    }


    //TODO: 예외처리 서비스 레이어 이동 필요
    public void start(LocalTime startedTime) {
        if (this.startedTime != null) throw new IllegalStateException("이미 시작된 배정 정보입니다.");
        this.startedTime = startedTime;
    }

    public void finish(LocalTime endedTime) {
        if (this.endedTime != null) throw new IllegalStateException("이미 종료된 배정 정보입니다.");
        this.endedTime = endedTime;
    }
}
