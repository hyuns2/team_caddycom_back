package com.flash21.caddycom.entity.golfFieldDetail;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE course SET deleted = true WHERE id = ?")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer totalHoles;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hole> holes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="FormationId")
    private Formation formation;

    @ColumnDefault("false")
    private boolean deleted;

    @Builder
    public Course(String name, int totalHoles, Formation formation) {
        this.name = name;
        this.totalHoles = totalHoles;
        this.formation = formation;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateTotalHoles(Integer newTotalHoles) {
        this.totalHoles = newTotalHoles;
    }
}
