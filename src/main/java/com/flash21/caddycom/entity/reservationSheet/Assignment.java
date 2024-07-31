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
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    HouseCaddy caddy;

    private String caddyName;

    private String reason;


    public void cancel() {
        this.status = AssignmentStatus.CANCELED;
    }


    public void vacateCaddy(){
        this.caddy = null;
        this.caddyName = null;

    public void blockAssignment(String reason) {
        this.status = AssignmentStatus.BLOCKED;
        this.reason = reason;
    }

    public void cancelBlockAssignment(String reason) {
        this.status = AssignmentStatus.NOTHING;
        this.reason = "";
    }

    public void assignCaddy(HouseCaddy caddy) {
        this.caddyName = caddy.getName();
        this.houseCaddy = caddy;
        caddy.getAssignmentList().add(this);
    }
}
