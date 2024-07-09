package com.flash21.caddycom.entity.golfFieldDetail;

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
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    @Builder
    public Formation(String name) {
        this.name = name;
    }
}
