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

    private Integer num;

    private Integer par = 4;

    private Integer handicap = 0;

    @OneToMany(mappedBy = "hole", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tee> tees = new ArrayList<>();

    @OneToMany(mappedBy = "hole", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="courseId")
    private Course course;

    private String imageUrl;

    public Hole(Integer num, Course course) {
        this.num = num;
        this.course = course;
    }

    public void updateHandicap(Integer handicap) {
        this.handicap = handicap;
    }

    public void updatePar(Integer par) {
        this.par = par;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
