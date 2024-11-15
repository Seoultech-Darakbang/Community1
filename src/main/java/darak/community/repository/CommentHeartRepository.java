package darak.community.repository;

import darak.community.domain.heart.CommentHeart;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
}
