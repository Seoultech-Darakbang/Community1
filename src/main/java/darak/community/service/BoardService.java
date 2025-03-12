package darak.community.service;

import darak.community.domain.Board;
import java.util.List;

public interface BoardService {
    Long save(Board board);

    Board findById(Long boardId);

    Board findByName(String name);

    List<Board> findAll();

    List<Board> findByBoardCategoryId(Long categoryId);

}
