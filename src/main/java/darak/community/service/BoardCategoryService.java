package darak.community.service;

import darak.community.domain.BoardCategory;
import java.util.List;

public interface BoardCategoryService {

    void save(BoardCategory boardCategory);

    List<BoardCategory> findAll();

    BoardCategory findById(Long boardId);
}
