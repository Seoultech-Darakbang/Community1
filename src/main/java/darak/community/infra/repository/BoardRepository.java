package darak.community.infra.repository;

import darak.community.domain.board.Board;
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

    public Optional<Board> findByIdFetchCategory(Long id) {
        return Optional.ofNullable(
                em.createQuery("select b from Board b join fetch b.boardCategory where b.id = :id", Board.class)
                        .setParameter("id", id)
                        .getSingleResult());
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
        return em.createQuery(
                        "select b from Board b where b.boardCategory.id = :boardCategoryId order by b.priority asc, b.id asc",
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


    public Optional<Board> findTopPriorityBoardByCategory(Long categoryId) {
        List<Board> boards = em.createQuery(
                        "select b from Board b where b.boardCategory.id = :categoryId order by b.priority asc",
                        Board.class)
                .setParameter("categoryId", categoryId)
                .setMaxResults(1)
                .getResultList();
        return boards.isEmpty() ? Optional.empty() : Optional.of(boards.get(0));
    }

    public long count() {
        return em.createQuery("select count(b) from Board b", Long.class)
                .getSingleResult();
    }

    public void delete(Board board) {
        em.remove(board);
    }

    public Page<Board> findAllPaged(Pageable pageable) {
        List<Board> boards = em.createQuery("select b from Board b order by b.id asc", Board.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery("select count(b) from Board b", Long.class)
                .getSingleResult();

        return new PageImpl<>(boards, pageable, count);
    }
}
