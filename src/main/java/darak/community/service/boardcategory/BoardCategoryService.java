package darak.community.service.boardcategory;

import darak.community.service.boardcategory.request.BoardCategoryCreateServiceRequest;
import darak.community.service.boardcategory.response.BoardCategoryResponse;
import java.util.List;

public interface BoardCategoryService {

    void createBoardCategory(BoardCategoryCreateServiceRequest request);

    List<BoardCategoryResponse> findAll();

    BoardCategoryResponse findById(Long boardId);

    BoardCategoryResponse findByName(String name);

}
