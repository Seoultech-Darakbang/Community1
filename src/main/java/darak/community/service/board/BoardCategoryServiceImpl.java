package darak.community.service.board;

import darak.community.domain.board.BoardCategory;
import darak.community.infra.repository.BoardCategoryRepository;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCategoryServiceImpl implements BoardCategoryService {

    private final BoardCategoryRepository boardCategoryRepository;
    private final List<BoardCategory> boardCategories = new ArrayList<>();

    @Override
    @Transactional
    public void save(BoardCategory boardCategory) {
        boardCategoryRepository.save(boardCategory);
        refreshCache();
    }

    @Override
    public List<BoardCategory> findAll() {
        return new ArrayList<>(boardCategories);
    }

    @Override
    public BoardCategory findById(Long boardId) {
        return boardCategoryRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    @Override
    public BoardCategory findByName(String name) {
        return boardCategoryRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
    }

    public void refreshCache() {
        boardCategories.clear();
        boardCategories.addAll(boardCategoryRepository.findAll());
        Collections.sort(boardCategories);
    }

    @PostConstruct
    public void init() {
        refreshCache();
    }
}
