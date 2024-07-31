package com.flash21.caddycom.entity.schedule;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


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
    private LocalDate reservationAt;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

//    @Column(nullable = false)
//    private LocalDateTime startDateTime;
//
//    @Column(nullable = false)
//    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String teeOff;

    @Column(nullable = false)
    private Integer part;

    @Column(nullable = false)
    private DateStatus dateStatus;

//    @Column(nullable = false)
//    private Boolean isAssigned;

    @Column(nullable = false)
    private Integer totalCnt;

    @Column(nullable = false)
    private Integer blockedCnt;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Assignment> assignments;

    public void setDateStatus(DateStatus dateStatus) {
        this.dateStatus = dateStatus;
    }

    public void addBlockCount() {
        this.blockedCnt++;
    }
}
