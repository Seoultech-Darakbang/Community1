package darak.community.service;

import darak.community.domain.board.Board;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    // 통계 정보
    long getTotalMemberCount();

    long getTotalPostCount();

    long getTotalCommentCount();

    long getTotalCategoryCount();

    long getTotalBoardCount();

    // 카테고리 관리
    void createCategory(String name, Integer priority);

    void updateCategory(Long id, String name, Integer priority);

    void deleteCategory(Long id);

    // 게시판 관리
    Page<Board> getAllBoardsPaged(Pageable pageable);

    Page<Board> getBoardsByCategoryPaged(Long categoryId, Pageable pageable);

    void createBoard(String name, String description, Long categoryId, Integer priority);

    void updateBoard(Long id, String name, String description, Long categoryId, Integer priority);

    void deleteBoard(Long id);

    // 회원 관리
    Page<Member> getAllMembersPaged(Pageable pageable);

    Page<Member> searchMembers(String keyword, MemberGrade grade, Pageable pageable);

    void updateMemberGrade(Long memberId, MemberGrade grade);
} 