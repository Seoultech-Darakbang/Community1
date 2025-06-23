package darak.community.service;

import darak.community.domain.board.BoardCategory;
import java.util.List;

public interface BoardCategoryService {

    void save(BoardCategory boardCategory);

    List<BoardCategory> findAll();

    BoardCategory findById(Long boardId);

    BoardCategory findByName(String name);

    void refreshCache();
}
