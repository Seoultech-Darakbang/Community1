package darak.community.service.board;

import darak.community.domain.board.Board;
import darak.community.infra.repository.BoardRepository;
import darak.community.service.board.request.BoardCreateServiceRequest;
import darak.community.service.board.response.BoardResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    public BoardResponse findBoardBy(Long boardId) {
        return boardRepository.findById(boardId)
                .map(BoardResponse::of)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    @Override
    public void createBoard(BoardCreateServiceRequest request) {
        boardRepository.save(request.toEntity());
    }

    @Override
    public List<BoardResponse> findBoardsBy(Long categoryId) {
        List<Board> boards = boardRepository.findByBoardCategoryId(categoryId);
        return boards.stream()
                .map(BoardResponse::of)
                .toList();
    }

    @Override
    public List<BoardResponse> findOrderedBoardsBy(Long categoryId) {
        List<Board> boards = boardRepository.findByBoardCategoryId(categoryId);
        return boards.stream()
                .map(BoardResponse::of)
                .sorted()
                .toList();
    }

    @Override
    public BoardResponse findTopPriorityBoardBy(Long categoryId) {
        Board board = boardRepository.findTopPriorityBoardByCategory(categoryId).orElseThrow(() ->
                new IllegalArgumentException("해당 카테고리에 속하는 게시판이 존재하지 않습니다"));
        return BoardResponse.of(board);
    }
}
