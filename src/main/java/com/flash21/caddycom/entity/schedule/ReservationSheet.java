package com.flash21.caddycom.entity.schedule;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.schedule.converter.LocalTimeListConverter;
import com.flash21.caddycom.entity.schedule.converter.LongListConverter;
import com.flash21.caddycom.entity.schedule.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "reservation_sheet")
public class ReservationSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private GolfField golfField;

    @Convert(converter = LongListConverter.class)
    @Column(nullable = false)
    private List<Long> courseIds;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Convert(converter = LocalTimeListConverter.class)
    @Column(nullable = false)
    private List<LocalTime> startTimes;

    @Convert(converter = LocalTimeListConverter.class)
    @Column(nullable = false)
    private List<LocalTime> endTimes;

    @Convert(converter = StringListConverter.class)
    @Column(nullable = false)
    private List<String> teeOffs;

    @OneToMany(mappedBy = "reservationSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    public void updateAll(GolfField golfField, List<Long> courseIds, LocalDate startDate, LocalDate endDate,
                       List<LocalTime> startTimes, List<LocalTime> endTimes, List<String> teeOffs) {
        this.golfField = golfField;
        this.courseIds = courseIds;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTimes = startTimes;
        this.endTimes = endTimes;
        this.teeOffs = teeOffs;
    }

    public void removeCourse(List<Long> deleteCourseIds) {
        for(Long courseId : courseIds) {
            if(deleteCourseIds.contains(courseId)) {
                courseIds.remove(courseId);
            }
        }
    }
}
