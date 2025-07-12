package darak.community.service.boardcategory;

import darak.community.service.boardcategory.request.BoardCategoryCreateServiceRequest;
import darak.community.service.boardcategory.request.BoardCategoryUpdateServiceRequest;
import darak.community.service.boardcategory.response.BoardCategoryResponse;
import java.util.List;

public interface BoardCategoryService {

    List<BoardCategoryResponse> findAll();

    BoardCategoryResponse findById(Long boardId);

    BoardCategoryResponse findByName(String name);

    void createBoardCategory(BoardCategoryCreateServiceRequest request);

    void updateBoardCategory(BoardCategoryUpdateServiceRequest request);

    void deleteCategory(Long id);

    long getTotalCategoryCount();

    long getFirstBoardIdByCategoryId(Long categoryId);
}
