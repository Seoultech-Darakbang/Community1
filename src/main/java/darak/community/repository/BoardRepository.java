package darak.community.repository;

import darak.community.domain.Board;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public List<Board> findAll() {
        return em.createQuery("select b from Board b", Board.class)
                .getResultList();
    }

    public List<Board> findByBoardCategoryId(Long boardCategoryId) {
        return em.createQuery("select b from Board b where b.boardCategory.id = :boardCategoryId order by b.id asc",
                        Board.class)
                .setParameter("boardCategoryId", boardCategoryId)
                .getResultList();
    }

    public Page<Board> findByBoardCategoryIdPaged(Long boardCategoryId, Pageable pageable) {
        List<Board> boards = em.createQuery(
                        "select b from Board b where b.boardCategory.id = :boardCategoryId order by b.id asc",
                        Board.class)
                .setParameter("boardCategoryId", boardCategoryId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery("select count(b) from Board b where b.boardCategory.id = :boardCategoryId",
                        Long.class)
                .setParameter("boardCategoryId", boardCategoryId)
                .getSingleResult();

        return new PageImpl<>(boards, pageable, count);
    }


    public Optional<Board> findByIdWithCategoryAndBoards(Long boardId) {
        return Optional.ofNullable(em.createQuery("select distinct b from Board b "
                        + "join fetch b.boardCategory bc "
                        + "join fetch bc.boards bcc "
                        + "where b.id = :boardId", Board.class)
                .setParameter("boardId", boardId)
                .getSingleResult());
    }
}
