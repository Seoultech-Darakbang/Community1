package darak.community.service;

import darak.community.domain.board.Board;
import darak.community.domain.board.BoardCategory;
import darak.community.repository.BoardCategoryRepository;
import darak.community.repository.BoardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final BoardCategoryService boardCategoryService;

    @Transactional
    @Override
    public void save(Board board) {
        boardRepository.save(board);
        // Board 저장 후 BoardCategory 캐시 새로고침해야 함. board 는 메모리에서 조회할거임
        if (boardCategoryService instanceof BoardCategoryServiceImpl) {
            ((BoardCategoryServiceImpl) boardCategoryService).refreshCache();
        }
    }

    @Override
    public Board findById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    @Override
    public Board findByName(String name) {
        return boardRepository.findByName(name).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    @Override
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    @Override
    public List<Board> findByBoardCategoryId(Long categoryId) {
        return boardRepository.findByBoardCategoryId(categoryId);
    }

    @Override
    public List<Board> findBoardsByCategory(String categoryName) {
        return boardCategoryRepository.findByName(categoryName)
                .map(BoardCategory::getId)
                .map(boardRepository::findByBoardCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다"));
    }

    @Override
    public Page<Board> findBoardsByCategoryPaged(String categoryName, Pageable pageable) {

        return boardCategoryRepository.findByName(categoryName)
                .map(BoardCategory::getId)
                .map(c -> boardRepository.findByBoardCategoryIdPaged(c, pageable))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다"));

    }

    @Override
    public List<Board> findBoardsByCategoryId(Long categoryId) {
        return boardRepository.findByBoardCategoryId(categoryId);
    }

    @Override
    public Board findBoardAndCategoryWithBoardId(Long boardId) {
        return boardRepository.findByIdWithCategoryAndBoards(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다")); // 한방 쿼리로 다 땡겨옴
    }

    @Override
    public Board findTopPriorityBoardByCategory(Long categoryId) {
        return boardRepository.findTopPriorityBoardByCategory(categoryId)
                .orElse(null);
    }
}
