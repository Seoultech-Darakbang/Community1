package darak.community.service.board;

import darak.community.core.auth.ServiceAuth;
import darak.community.domain.board.Board;
import darak.community.domain.board.BoardCategory;
import darak.community.domain.member.MemberGrade;
import darak.community.infra.repository.BoardCategoryRepository;
import darak.community.infra.repository.BoardRepository;
import darak.community.infra.repository.PostRepository;
import darak.community.infra.repository.dto.PostWithAllDto;
import darak.community.service.board.request.BoardCreateServiceRequest;
import darak.community.service.board.request.BoardUpdateServiceRequest;
import darak.community.service.board.response.BoardAdminResponse;
import darak.community.service.board.response.BoardResponse;
import darak.community.service.boardcategory.response.BoardCategoryResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final PostRepository postRepository;

    @ServiceAuth(MemberGrade.ADMIN)
    @Override
    @Transactional
    public void createBoard(BoardCreateServiceRequest request) {
        Board board = request.toEntity();
        BoardCategory boardCategory = findBoardCategoryBy(request.getBoardCategoryId());
        board.registerCategory(boardCategory);
        boardRepository.save(board);
    }

    @ServiceAuth(MemberGrade.ADMIN)
    @Override
    @Transactional
    public void updateBoard(BoardUpdateServiceRequest request) {
        Board board = findBoardBy(request.getBoardId());
        BoardCategory boardCategory = findBoardCategoryBy(request.getBoardCategoryId());
        board.updateBoard(request.getName(), request.getDescription(), boardCategory, request.getPriority());
    }

    @ServiceAuth(MemberGrade.ADMIN)
    @Override
    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = findBoardBy(boardId);
        boardRepository.delete(board);
    }

    @Override
    public BoardResponse findBoardInfoBy(Long boardId) {
        return boardRepository.findById(boardId)
                .map(BoardResponse::of)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    @Override
    public List<BoardResponse> findBoardsBy(Long categoryId) {
        List<Board> boards = boardRepository.findByBoardCategoryId(categoryId);
        return boards.stream()
                .map(BoardResponse::of)
                .toList();
    }

    @Override
    public Map<BoardCategoryResponse, List<BoardResponse>> findBoardsGroupedByCategory() {
        return boardRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        board -> BoardCategoryResponse.of(board.getBoardCategory()),
                        Collectors.mapping(BoardResponse::of, Collectors.toList())
                ));
    }

    @Override
    public Map<BoardResponse, List<PostWithAllDto>> findRecentPostsGroupedByBoardLimit(int limit) {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .collect(Collectors.toMap(
                        BoardResponse::of,
                        board -> postRepository.findPostsWithMetaByBoardId(board.getId(), PageRequest.of(0, limit))
                                .stream()
                                .toList()));
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

    @Override
    public Page<BoardResponse> getAllBoardsPaged(Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAllPaged(pageable);
        return boardPage.map(BoardResponse::of);
    }

    @Override
    public Page<BoardResponse> getBoardsByCategoryPaged(Long categoryId, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findByBoardCategoryIdPaged(categoryId, pageable);
        return boardPage.map(BoardResponse::of);
    }

    @Override
    public Page<BoardAdminResponse> getAllBoardsWithCategoryPaged(Pageable pageable) {
        return boardRepository.findAllPaged(pageable).map(BoardAdminResponse::of);
    }

    @Override
    public Page<BoardAdminResponse> getBoardsWithCategoryByCategoryPaged(Long categoryId, Pageable pageable) {
        return boardRepository.findByBoardCategoryIdPaged(categoryId, pageable)
                .map(BoardAdminResponse::of);
    }

    @Override
    public BoardAdminResponse findBoardAdminInfoBy(Long boardId) {
        return BoardAdminResponse.of(findBoardBy(boardId));
    }

    @Override
    public long getTotalBoardCount() {
        return boardRepository.count();
    }


    private Board findBoardBy(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    private BoardCategory findBoardCategoryBy(Long boardCategoryId) {
        return boardCategoryRepository.findById(boardCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판 카테고리입니다."));
    }
}
