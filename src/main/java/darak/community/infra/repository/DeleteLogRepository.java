package darak.community.infra.repository;

import darak.community.domain.log.DeleteLog;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleteLogRepository {

    private final EntityManager em;

    public void save(DeleteLog log) {
        em.persist(log);
    }
}
