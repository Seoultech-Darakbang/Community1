package darak.community.service;

import darak.community.domain.BoardCategory;
import darak.community.repository.BoardCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCategoryServiceImpl implements BoardCategoryService {

    private final BoardCategoryRepository boardCategoryRepository;

    @Override
    public List<BoardCategory> findAll() {
        return boardCategoryRepository.findAll();
    }

    @Override
    public BoardCategory findById(Long boardId) {
        return boardCategoryRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }
}
