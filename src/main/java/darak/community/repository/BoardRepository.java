package darak.community.repository;

import darak.community.domain.Board;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
    private final EntityManager em;

    public void save(Board board) {
        em.persist(board);
    }

    public Optional<Board> findById(Long id) {
        Board board = em.find(Board.class, id);
        return Optional.ofNullable(board);
    }

    public Optional<Board> findByName(String name) {
        return em.createQuery("select b from Board b where b.name = :name", Board.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findAny();
    }
}
