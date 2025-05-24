package darak.community.repository;

import darak.community.domain.Comment;
import darak.community.domain.heart.CommentHeart;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentHeartRepository {
    private final EntityManager em;

    public void save(CommentHeart commentHeart) {
        em.persist(commentHeart);
    }

    public void delete(CommentHeart commentHeart) {
        em.remove(commentHeart);
    }

    public List<CommentHeart> findByMemberId(Long memberId) {
        return em.createQuery("select ch from CommentHeart ch where ch.member.id = :memberId", CommentHeart.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public Optional<CommentHeart> findByCommentIdAndMemberId(Long commentId, Long memberId) {
        List<CommentHeart> result = em.createQuery(
                        "select ch from CommentHeart ch where ch.comment.id = :commentId and ch.member.id = :memberId",
                        CommentHeart.class)
                .setParameter("commentId", commentId)
                .setParameter("memberId", memberId)
                .getResultList();
        return result.stream().findAny();
    }

    public int countByCommentId(Long commentId) {
        return em.createQuery("select count(ch) from CommentHeart ch where ch.comment.id = :commentId", Long.class)
                .setParameter("commentId", commentId)
                .getSingleResult()
                .intValue();
    }

    public Page<Comment> findLikedCommentsByMember(Long memberId, Pageable pageable) {
        // 좋아요한 댓글 조회 쿼리
        String jpql = "SELECT c FROM CommentHeart ch " +
                     "JOIN ch.comment c " +
                     "WHERE ch.member.id = :memberId " +
                     "ORDER BY ch.createdDate DESC";
        
        TypedQuery<Comment> query = em.createQuery(jpql, Comment.class)
                .setParameter("memberId", memberId);
        
        // 전체 개수 조회
        String countJpql = "SELECT COUNT(ch) FROM CommentHeart ch WHERE ch.member.id = :memberId";
        Long totalCount = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
        
        // 페이지네이션 적용
        query.setFirstResult((int) pageable.getOffset())
             .setMaxResults(pageable.getPageSize());
        
        List<Comment> comments = query.getResultList();
        
        return new PageImpl<>(comments, pageable, totalCount);
    }
}
