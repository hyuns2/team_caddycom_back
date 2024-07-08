package com.flash21.caddycom.entity;

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

    private int par;

    private int handicap;

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
        this.par = 4;
        this.tees.addAll(List.of(
                new Tee("블랙", 320, this),
                new Tee("블루", 290, this),
                new Tee("화이트", 270, this),
                new Tee("레드", 250, this),
                new Tee("그린", 230, this)
                ));
    }

    public void updateHandicap(int handicap) {
        this.handicap = handicap;
    }

    public void updatePar(int par) {
        this.par = par;
    }
}
