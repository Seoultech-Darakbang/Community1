package darak.community.service;

import darak.community.domain.BoardCategory;
import java.util.List;

public interface BoardCategoryService {

    List<BoardCategory> findAll();

    BoardCategory findById(Long boardId);

}
