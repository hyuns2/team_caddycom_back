package com.flash21.caddycom.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class TipInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name="HoleId")
    private Hole hole;

    public TipInfo(String title, String content, Hole hole) {
        this.title = title;
        this.content = content;
        this.hole = hole;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
