package com.flash21.caddycom.entity.schedule;

import com.flash21.caddycom.entity.caddy.Caddy;
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
    private Caddy caddy;

    private String caddyName;

    private String reason;

    private LocalTime startedTime;

    private LocalTime endedTime;

    public void cancel(String reason) {
        if (reason != null && !reason.isEmpty())
            this.reason = reason;
        this.status = AssignmentStatus.CANCELED;
    }

    public void requestCancel(String reason) {
        if (reason != null && !reason.isEmpty())
            this.reason = reason;
        this.status = AssignmentStatus.REQUESTED;
    }


    public void vacateCaddy() {
        this.caddy = null;
        this.caddyName = null;
    }

    public void blockAssignment(String reason) {
        if (this.status != AssignmentStatus.BLOCKED) {
            this.status = AssignmentStatus.BLOCKED;
            this.reason = reason;
            this.schedule.addBlockCount();
        } else if (this.status == AssignmentStatus.BLOCKED) {
            this.reason = reason;
        }
    }

    public void cancelBlock() {
        if (this.status == AssignmentStatus.BLOCKED) {
            this.status = AssignmentStatus.NOTHING;
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
        if (this.status == AssignmentStatus.BLOCKED) return;
        this.status = AssignmentStatus.ASSIGNED;
    }

    public void updateByDeletedSchedule() {
        this.schedule = null;
    }

    public void terminate(LocalTime endedTime) {
        this.endedTime = endedTime;
    }
}
