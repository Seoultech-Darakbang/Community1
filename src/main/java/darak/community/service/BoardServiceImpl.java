package darak.community.service;

import darak.community.domain.Board;
import darak.community.domain.BoardCategory;
import darak.community.repository.BoardCategoryRepository;
import darak.community.repository.BoardRepository;
import java.util.Collections;
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

    @Transactional
    @Override
    public Long save(Board board) {
        boardRepository.save(board);
        return board.getId();
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
        BoardCategory category = boardCategoryRepository.findByName(categoryName)
                .orElse(null);
        if (category == null) {
            return Collections.emptyList();
        }
        return boardRepository.findByBoardCategoryId(category.getId());
    }
    
    @Override
    public Page<Board> findBoardsByCategoryPaged(String categoryName, Pageable pageable) {
        BoardCategory category = boardCategoryRepository.findByName(categoryName)
                .orElse(null);
        if (category == null) {
            return Page.empty(pageable);
        }
        return boardRepository.findByBoardCategoryIdPaged(category.getId(), pageable);
    }
}
