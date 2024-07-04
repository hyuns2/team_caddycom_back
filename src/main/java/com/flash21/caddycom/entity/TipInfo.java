package com.flash21.caddycom.entity;

import jakarta.persistence.*;

@Entity
public class TipInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name="HoleId")
    private Hole hole;
}
