package com.flash21.caddycom.entity.schedule;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
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


    public void cancel(String reason) {
        if (reason != null && !reason.isEmpty())
            this.reason = reason;
        this.status = AssignmentStatus.CANCELED;
    }


    public void vacateCaddy() {
        this.houseCaddy = null;
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
            this.houseCaddy = null;
            this.schedule.subBlockCount();
        }
    }

    public void assignHouseCaddy(HouseCaddy caddy) {
        this.caddyName = caddy.getName();
        this.houseCaddy = caddy;
        caddy.getAssignmentList().add(this);
        if (this.status == AssignmentStatus.BLOCKED) return;
        this.status = AssignmentStatus.ASSIGNED;
    }
}
