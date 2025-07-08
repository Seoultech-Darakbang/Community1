package darak.community.service.board;

import darak.community.service.board.request.BoardCreateServiceRequest;
import darak.community.service.board.response.BoardResponse;
import java.util.List;

public interface BoardService {

    void createBoard(BoardCreateServiceRequest request);

    BoardResponse findBoardBy(Long boardId);

    List<BoardResponse> findBoardsBy(Long categoryId);

    List<BoardResponse> findOrderedBoardsBy(Long categoryId);

    BoardResponse findTopPriorityBoardBy(Long categoryId);
}
