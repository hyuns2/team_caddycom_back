package com.flash21.caddycom.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Tee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int distance;

    @ManyToOne
    @JoinColumn(name="HoleId")
    private Hole hole;

    public Tee(String name, int distance, Hole hole) {
        this.name = name;
        this.distance = distance;
        this.hole = hole;
    }
}
