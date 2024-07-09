package com.flash21.caddycom.entity.golfFieldDetail;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Hole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int num;

    private int par = 4;

    private int handicap = 0;

    @OneToMany(mappedBy = "hole", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Tee> tees = new ArrayList<>();

    @OneToMany(mappedBy = "hole", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<TipInfo> tipInfos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="courseId")
    private Course course;

    public Hole(int num, Course course) {
        this.num = num;
        this.course = course;
    }

    public void updateHandicap(int handicap) {
        this.handicap = handicap;
    }

    public void updatePar(int par) {
        this.par = par;
    }
}
