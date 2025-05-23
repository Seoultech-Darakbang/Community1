package darak.community.service;

import darak.community.domain.Board;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    void save(Board board);

    Board findById(Long boardId);

    Board findByName(String name);

    List<Board> findAll();

    List<Board> findByBoardCategoryId(Long categoryId);

    List<Board> findBoardsByCategory(String categoryName);

    Page<Board> findBoardsByCategoryPaged(String categoryName, Pageable pageable);

    List<Board> findBoardsByCategoryId(Long categoryId);
}
