package darak.community.repository;

import darak.community.domain.Board;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminRepository {

    private final EntityManager em;

    // 통계 정보
    public long getTotalMemberCount() {
        return em.createQuery("SELECT COUNT(m) FROM Member m", Long.class)
                .getSingleResult();
    }

    public long getTotalPostCount() {
        return em.createQuery("SELECT COUNT(p) FROM Post p", Long.class)
                .getSingleResult();
    }

    public long getTotalCommentCount() {
        return em.createQuery("SELECT COUNT(c) FROM Comment c", Long.class)
                .getSingleResult();
    }

    public long getTotalCategoryCount() {
        return em.createQuery("SELECT COUNT(bc) FROM BoardCategory bc", Long.class)
                .getSingleResult();
    }

    public long getTotalBoardCount() {
        return em.createQuery("SELECT COUNT(b) FROM Board b", Long.class)
                .getSingleResult();
    }

    // 카테고리 관리
    public void updateCategory(Long id, String name, Integer priority) {
        em.createQuery("UPDATE BoardCategory bc SET bc.name = :name, bc.priority = :priority WHERE bc.id = :id")
                .setParameter("name", name)
                .setParameter("priority", priority)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void deleteCategory(Long id) {
        em.createQuery("DELETE FROM BoardCategory bc WHERE bc.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    // 게시판 관리
    public Page<Board> getAllBoardsPaged(Pageable pageable) {
        List<Board> boards = em.createQuery(
                "SELECT b FROM Board b " +
                "JOIN FETCH b.boardCategory bc " +
                "ORDER BY bc.priority ASC, b.priority ASC", Board.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery("SELECT COUNT(b) FROM Board b", Long.class)
                .getSingleResult();

        return new PageImpl<>(boards, pageable, count);
    }

    public void updateBoard(Long id, String name, String description, Long categoryId, Integer priority) {
        em.createQuery("UPDATE Board b SET b.name = :name, b.description = :description, " +
                      "b.boardCategory.id = :categoryId, b.priority = :priority WHERE b.id = :id")
                .setParameter("name", name)
                .setParameter("description", description)
                .setParameter("categoryId", categoryId)
                .setParameter("priority", priority)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void deleteBoard(Long id) {
        em.createQuery("DELETE FROM Board b WHERE b.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public boolean hasPostsInBoard(Long boardId) {
        Long count = em.createQuery("SELECT COUNT(p) FROM Post p WHERE p.board.id = :boardId", Long.class)
                .setParameter("boardId", boardId)
                .getSingleResult();
        return count > 0;
    }

    // 회원 관리
    public Page<Member> getAllMembersPaged(Pageable pageable) {
        List<Member> members = em.createQuery(
                "SELECT m FROM Member m ORDER BY m.createdDate DESC", Member.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery("SELECT COUNT(m) FROM Member m", Long.class)
                .getSingleResult();

        return new PageImpl<>(members, pageable, count);
    }

    public Page<Member> searchMembers(String keyword, MemberGrade grade, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT m FROM Member m WHERE 1=1");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql.append(" AND (m.name LIKE :keyword OR m.loginId LIKE :keyword OR m.email LIKE :keyword)");
        }
        
        if (grade != null) {
            jpql.append(" AND m.memberGrade = :grade");
        }
        
        jpql.append(" ORDER BY m.createdDate DESC");

        var query = em.createQuery(jpql.toString(), Member.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        
        if (grade != null) {
            query.setParameter("grade", grade);
        }

        List<Member> members = query.getResultList();

        // Count 쿼리
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(m) FROM Member m WHERE 1=1");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            countJpql.append(" AND (m.name LIKE :keyword OR m.loginId LIKE :keyword OR m.email LIKE :keyword)");
        }
        
        if (grade != null) {
            countJpql.append(" AND m.memberGrade = :grade");
        }

        var countQuery = em.createQuery(countJpql.toString(), Long.class);

        if (keyword != null && !keyword.trim().isEmpty()) {
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }
        
        if (grade != null) {
            countQuery.setParameter("grade", grade);
        }

        Long count = countQuery.getSingleResult();

        return new PageImpl<>(members, pageable, count);
    }

    public void updateMemberGrade(Long memberId, MemberGrade grade) {
        em.createQuery("UPDATE Member m SET m.memberGrade = :grade WHERE m.id = :id")
                .setParameter("grade", grade)
                .setParameter("id", memberId)
                .executeUpdate();
    }
} 