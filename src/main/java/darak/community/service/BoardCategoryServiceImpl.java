package darak.community.service;

import darak.community.domain.BoardCategory;
import darak.community.repository.BoardCategoryRepository;
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
        boardCategories.add(boardCategory);
        Collections.sort(boardCategories);
    }

    @Override
    public List<BoardCategory> findAll() {
        return boardCategories;
    }

    @Override
    public BoardCategory findById(Long boardId) {
        return boardCategoryRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    @PostConstruct
    public void init() {
        boardCategories.addAll(boardCategoryRepository.findAll());
        Collections.sort(boardCategories);
    }
}
