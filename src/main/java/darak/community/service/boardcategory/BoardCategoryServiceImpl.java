package darak.community.service.boardcategory;

import darak.community.domain.board.BoardCategory;
import darak.community.infra.repository.BoardCategoryRepository;
import darak.community.infra.repository.BoardRepository;
import darak.community.service.boardcategory.request.BoardCategoryCreateServiceRequest;
import darak.community.service.boardcategory.response.BoardCategoryResponse;
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
    private final BoardRepository boardRepository;
    private final List<BoardCategory> sortedBoardCategories = new ArrayList<>();

    @Override
    @Transactional
    public void createBoardCategory(BoardCategoryCreateServiceRequest request) {
        boardCategoryRepository.save(request.toEntity());
        refreshCache();
    }

    @Override
    public List<BoardCategoryResponse> findAll() {
        return sortedBoardCategories.stream()
                .map(BoardCategoryResponse::of)
                .toList();
    }

    @Override
    public BoardCategoryResponse findById(Long boardId) {
        BoardCategory boardCategory = boardCategoryRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
        return BoardCategoryResponse.of(boardCategory);
    }

    @Override
    public BoardCategoryResponse findByName(String name) {
        BoardCategory boardCategory = boardCategoryRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        return BoardCategoryResponse.of(boardCategory);
    }

    @PostConstruct
    public void init() {
        refreshCache();
    }

    private void refreshCache() {
        sortedBoardCategories.clear();
        sortedBoardCategories.addAll(boardCategoryRepository.findAll());
        Collections.sort(sortedBoardCategories);
    }
}
