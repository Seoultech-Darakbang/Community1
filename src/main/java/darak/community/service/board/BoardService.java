package darak.community.service.board;

import darak.community.infra.repository.dto.PostWithMetaDto;
import darak.community.service.board.request.BoardCreateServiceRequest;
import darak.community.service.board.request.BoardUpdateServiceRequest;
import darak.community.service.board.response.BoardResponse;
import darak.community.service.boardcategory.response.BoardCategoryResponse;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    void createBoard(BoardCreateServiceRequest request);

    void updateBoard(BoardUpdateServiceRequest request);

    void deleteBoard(Long boardId);

    BoardResponse readBoardBy(Long boardId);

    List<BoardResponse> findBoardsBy(Long categoryId);

    Map<BoardCategoryResponse, List<BoardResponse>> findBoardsGroupedByCategory();

    Map<BoardResponse, List<PostWithMetaDto>> findRecentPostsGroupedByBoardLimit(int limit);

    List<BoardResponse> findOrderedBoardsBy(Long categoryId);

    BoardResponse findTopPriorityBoardBy(Long categoryId);

    Page<BoardResponse> getAllBoardsPaged(Pageable pageable);

    Page<BoardResponse> getBoardsByCategoryPaged(Long categoryId, Pageable pageable);

    long getTotalBoardCount();

}
