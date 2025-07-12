package darak.community.service.boardcategory;

import darak.community.core.auth.ServiceAuth;
import darak.community.domain.board.Board;
import darak.community.domain.board.BoardCategory;
import darak.community.domain.member.MemberGrade;
import darak.community.infra.repository.BoardCategoryRepository;
import darak.community.infra.repository.BoardRepository;
import darak.community.service.boardcategory.request.BoardCategoryCreateServiceRequest;
import darak.community.service.boardcategory.request.BoardCategoryUpdateServiceRequest;
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
    @ServiceAuth(MemberGrade.ADMIN)
    public void createBoardCategory(BoardCategoryCreateServiceRequest request) {
        boardCategoryRepository.save(request.toEntity());
        refreshCache();
    }

    @Transactional
    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public void updateBoardCategory(BoardCategoryUpdateServiceRequest request) {
        BoardCategory boardCategory = findBoardCategoryBy(request.getBoardCategoryId());
        boardCategory.updateCategory(request.getName(), request.getPriority());
        refreshCache();
    }

    @Transactional
    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public void deleteCategory(Long id) {
        BoardCategory boardCategory = findBoardCategoryBy(id);
        boardCategoryRepository.delete(boardCategory);
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
        return BoardCategoryResponse.of(findBoardCategoryBy(boardId));
    }

    @Override
    public BoardCategoryResponse findByName(String name) {
        BoardCategory boardCategory = boardCategoryRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        return BoardCategoryResponse.of(boardCategory);
    }

    @Override
    public long getTotalCategoryCount() {
        return boardCategoryRepository.count();
    }

    @Override
    public long getFirstBoardIdByCategoryId(Long categoryId) {
        List<Board> boards = boardRepository.findByBoardCategoryId(categoryId);
        Board board = boards.stream().sorted()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        return board.getId();
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

    private BoardCategory findBoardCategoryBy(Long boardCategoryId) {
        return boardCategoryRepository.findById(boardCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }
}
