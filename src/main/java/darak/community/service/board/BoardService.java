package darak.community.service.board;

import darak.community.service.board.request.BoardCreateServiceRequest;
import darak.community.service.board.request.BoardUpdateServiceRequest;
import darak.community.service.board.response.BoardResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    void createBoard(BoardCreateServiceRequest request);

    void updateBoard(BoardUpdateServiceRequest request);

    void deleteBoard(Long boardId);

    BoardResponse readBoardBy(Long boardId);

    List<BoardResponse> findBoardsBy(Long categoryId);

    List<BoardResponse> findOrderedBoardsBy(Long categoryId);

    BoardResponse findTopPriorityBoardBy(Long categoryId);

    Page<BoardResponse> getAllBoardsPaged(Pageable pageable);

    Page<BoardResponse> getBoardsByCategoryPaged(Long categoryId, Pageable pageable);

    long getTotalBoardCount();
}
