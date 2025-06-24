package darak.community.service;

import darak.community.domain.board.Board;
import darak.community.domain.board.BoardCategory;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.infra.repository.AdminRepository;
import darak.community.infra.repository.BoardCategoryRepository;
import darak.community.infra.repository.BoardRepository;
import darak.community.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BoardCategoryService boardCategoryService;

    @Override
    public long getTotalMemberCount() {
        return adminRepository.getTotalMemberCount();
    }

    @Override
    public long getTotalPostCount() {
        return adminRepository.getTotalPostCount();
    }

    @Override
    public long getTotalCommentCount() {
        return adminRepository.getTotalCommentCount();
    }

    @Override
    public long getTotalCategoryCount() {
        return adminRepository.getTotalCategoryCount();
    }

    @Override
    public long getTotalBoardCount() {
        return adminRepository.getTotalBoardCount();
    }

    @Override
    @Transactional
    public void createCategory(String name, Integer priority) {
        // 중복 이름 체크
        if (boardCategoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 카테고리명입니다.");
        }

        BoardCategory category = BoardCategory.builder()
                .name(name)
                .priority(priority != null ? priority : 999)
                .build();

        boardCategoryService.save(category);
        log.info("카테고리 생성: {}", name);
    }

    @Override
    @Transactional
    public void updateCategory(Long id, String name, Integer priority) {
        BoardCategory category = boardCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 중복 이름 체크 (자기 자신 제외)
        boardCategoryRepository.findByName(name)
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new IllegalArgumentException("이미 존재하는 카테고리명입니다.");
                });

        BoardCategory updatedCategory = BoardCategory.builder()
                .name(name)
                .priority(priority != null ? priority : 999)
                .build();

        adminRepository.updateCategory(id, name, priority);
        boardCategoryService.refreshCache();

        log.info("카테고리 수정: ID={}, 이름={}", id, name);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        BoardCategory category = boardCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        if (!category.getBoards().isEmpty()) {
            throw new IllegalStateException("게시판이 있는 카테고리는 삭제할 수 없습니다.");
        }

        adminRepository.deleteCategory(id);
        boardCategoryService.refreshCache();

        log.info("카테고리 삭제: ID={}, 이름={}", id, category.getName());
    }

    @Override
    public Page<Board> getAllBoardsPaged(Pageable pageable) {
        return adminRepository.getAllBoardsPaged(pageable);
    }

    @Override
    public Page<Board> getBoardsByCategoryPaged(Long categoryId, Pageable pageable) {
        return boardRepository.findByBoardCategoryIdPaged(categoryId, pageable);
    }

    @Override
    @Transactional
    public void createBoard(String name, String description, Long categoryId, Integer priority) {
        // 중복 이름 체크
        if (boardRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 게시판명입니다.");
        }

        BoardCategory category = boardCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        Board board = Board.builder()
                .name(name)
                .description(description)
                .boardCategory(category)
                .priority(priority != null ? priority : 999)
                .build();

        boardRepository.save(board);
        boardCategoryService.refreshCache();

        log.info("게시판 생성: {}", name);
    }

    @Override
    @Transactional
    public void updateBoard(Long id, String name, String description, Long categoryId, Integer priority) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

        boardRepository.findByName(name)
                .filter(b -> !b.getId().equals(id))
                .ifPresent(b -> {
                    throw new IllegalArgumentException("이미 존재하는 게시판명입니다.");
                });

        BoardCategory category = boardCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        adminRepository.updateBoard(id, name, description, categoryId, priority);
        boardCategoryService.refreshCache();

        log.info("게시판 수정: ID={}, 이름={}", id, name);
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

        if (adminRepository.hasPostsInBoard(id)) {
            throw new IllegalStateException("게시글이 있는 게시판은 삭제할 수 없습니다.");
        }

        adminRepository.deleteBoard(id);
        boardCategoryService.refreshCache();

        log.info("게시판 삭제: ID={}, 이름={}", id, board.getName());
    }

    @Override
    public Page<Member> getAllMembersPaged(Pageable pageable) {
        return adminRepository.getAllMembersPaged(pageable);
    }

    @Override
    public Page<Member> searchMembers(String keyword, MemberGrade grade, Pageable pageable) {
        return adminRepository.searchMembers(keyword, grade, pageable);
    }

    @Override
    @Transactional
    public void updateMemberGrade(Long memberId, MemberGrade grade) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        adminRepository.updateMemberGrade(memberId, grade);

        log.info("회원 등급 변경: ID={}, 이름={}, 등급={}", memberId, member.getName(), grade);
    }
} 