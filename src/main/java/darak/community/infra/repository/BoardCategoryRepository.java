package darak.community.infra.repository;

import darak.community.domain.board.BoardCategory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardCategoryRepository {

    private final EntityManager em;

    public void save(BoardCategory boardCategory) {
        if (boardCategory.getId() == null) {
            em.persist(boardCategory);
        } else {
            em.merge(boardCategory);
        }
    }

    public List<BoardCategory> findAll() {
        return em.createQuery(
                        "select distinct bc from BoardCategory bc", BoardCategory.class)
                .getResultList();
    }

    public Optional<BoardCategory> findById(Long id) {
        BoardCategory boardCategory = em.find(BoardCategory.class, id);
        return Optional.ofNullable(boardCategory);
    }

    public Optional<BoardCategory> findByName(String name) {
        return em.createQuery("select bc from BoardCategory bc where bc.name = :name", BoardCategory.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    public long count() {
        return em.createQuery("select count(bc) from BoardCategory bc", Long.class)
                .getSingleResult();
    }

    public void delete(BoardCategory boardCategory) {
        em.remove(boardCategory);
    }
}
