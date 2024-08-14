package com.flash21.caddycom.entity.caddy;

import com.flash21.caddycom.entity.golfField.GolfField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchedFreeCaddy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private FreeCaddy freeCaddy;

    @ManyToOne(fetch = FetchType.LAZY)
    private GolfField golfField;

    public static MatchedFreeCaddy of(FreeCaddy freeCaddy, GolfField golfField) {
        return MatchedFreeCaddy.builder()
                .freeCaddy(freeCaddy)
                .golfField(golfField)
                .build();
    }

    public void setFreeCaddy(FreeCaddy freeCaddy) {
        this.freeCaddy = freeCaddy;
    }
}
