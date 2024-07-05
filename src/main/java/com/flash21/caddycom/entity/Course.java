package com.flash21.caddycom.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int totalHoles;

    @OneToMany(mappedBy = "course", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Hole> holes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="FormationId")
    private Formation formation;

    @Builder
    public Course(String name, int totalHoles, Formation formation) {
        this.name = name;
        this.totalHoles = totalHoles;
        this.formation = formation;
        setDefaultHoleInfos();
        formation.getCourses().add(this);
    }

    private void setDefaultHoleInfos() {
        for(int num = 1; num <= this.totalHoles; num++) {
            this.holes.add(new Hole(num, this));
        }
    }
}
