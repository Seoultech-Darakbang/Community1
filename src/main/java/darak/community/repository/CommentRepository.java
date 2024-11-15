package darak.community.repository;

import darak.community.domain.Comment;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final EntityManager em;

    public void save(Comment comment) {
        em.persist(comment);
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }

    public List<Comment> findByPostId(Long postId) {
        return em.createQuery("select c from Comment c where c.post.id = :postId", Comment.class)
            .setParameter("postId", postId)
            .getResultList();
    }

    public List<Comment> findByMemberId(Long memberId) {
        return em.createQuery("select c from Comment c where c.member.id = :memberId", Comment.class)
            .setParameter("memberId", memberId)
            .getResultList();
    }
}
