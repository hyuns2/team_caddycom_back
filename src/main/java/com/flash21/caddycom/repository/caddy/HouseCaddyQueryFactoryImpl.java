package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.flash21.caddycom.entity.caddy.QHouseCaddy.houseCaddy;

@Repository
@RequiredArgsConstructor
public class HouseCaddyQueryFactoryImpl implements HouseCaddyQueryFactory {

    private final JPAQueryFactory queryFactory;

    public List<HouseCaddy> findAllByGolfFieldIdAndSearchCond(Long golfFieldId, HouseCaddyRequestDto.CaddySearchCond searchCond) {
        return queryFactory
                .selectFrom(houseCaddy)
                .where(houseCaddy.golfField.id.eq(golfFieldId),
                        caddyNamePart(searchCond),
                        teamNamePart(searchCond))
                .fetch();
    }

    private BooleanExpression caddyNamePart(HouseCaddyRequestDto.CaddySearchCond searchCond) {
        return searchCond.getName() != null && !searchCond.getName().isEmpty()
                ? houseCaddy.name.containsIgnoreCase(searchCond.getName()) : null;
    }

    private BooleanExpression teamNamePart(HouseCaddyRequestDto.CaddySearchCond searchCond) {
        return searchCond.getTeam() != null && !searchCond.getTeam().isEmpty()
                ? houseCaddy.team.eq(searchCond.getTeam()) : null;

    }

}
