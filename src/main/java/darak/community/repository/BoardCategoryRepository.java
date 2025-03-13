package darak.community.repository;

import darak.community.domain.BoardCategory;
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
                "select distinct bc from BoardCategory bc left join fetch bc.boards", BoardCategory.class)
                .getResultList();
    }

    public Optional<BoardCategory> findById(Long id) {
        BoardCategory boardCategory = em.find(BoardCategory.class, id);
        return Optional.ofNullable(boardCategory);
    }

}
