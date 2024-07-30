package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyResponse;
import com.flash21.caddycom.dto.caddy.CaddySearchCond;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HouseCaddyQueryRepositoryImpl implements HouseCaddyQueryRepository {

    private final EntityManager em;

    @Override
    public List<HouseCaddyResponse> findAllByGoldFieldIdAndSort(Long goldFieldId, CaddySearchCond searchCond) {
        StringBuilder queryBuilder = new StringBuilder(
                "select new com.flash21.caddycom.dto.caddy.HouseCaddyResponse(hc.id, hc.team, hc.teamRole, hc.name, hc.holiday) " +
                        "from HouseCaddy hc " +
                        "where hc.golfField.id = :goldFieldId");

        boolean nameCond = searchCond.getName() != null && !searchCond.getName().isEmpty();
        boolean teamCond = searchCond.getTeam() != null && !searchCond.getTeam().isEmpty();

        if (nameCond) {
            queryBuilder.append(" and hc.name like :name");
        }
        if (teamCond) {
            queryBuilder.append(" and hc.team = :team");
        }

        Query query = em.createQuery(queryBuilder.toString(), HouseCaddyResponse.class);
        query.setParameter("goldFieldId", goldFieldId);

        if (nameCond) {
            query.setParameter("name", "%" + searchCond.getName() + "%");
        }
        if (teamCond) {
            query.setParameter("team", searchCond.getTeam());
        }

        return query.getResultList();
    }
}
