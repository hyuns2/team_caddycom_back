package com.flash21.caddycom.entity.golfFieldDetail;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Tee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer distance;

    @ManyToOne
    @JoinColumn(name="HoleId")
    private Hole hole;

    public void update(String name, Integer distance) {
        this.name = name;
        this.distance = distance;
    }
}
