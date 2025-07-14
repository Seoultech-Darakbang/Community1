package darak.community.infra.repository;

import darak.community.domain.log.AdminLog;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminLogRepository {

    private final EntityManager em;

    public void save(AdminLog log) {
        em.persist(log);
    }
}
