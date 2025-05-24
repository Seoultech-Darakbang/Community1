package darak.community.repository;

import darak.community.domain.Comment;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        return em.createQuery("select c from Comment c join fetch c.member where c.post.id = :postId order by c.createdDate", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public List<Comment> findParentCommentsByPostId(Long postId) {
        return em.createQuery("select c from Comment c join fetch c.member where c.post.id = :postId and c.parent is null order by c.createdDate", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public Page<Comment> findParentCommentsByPostId(Long postId, Pageable pageable) {
        Long totalCount = em.createQuery("select count(c) from Comment c where c.post.id = :postId and c.parent is null", Long.class)
                .setParameter("postId", postId)
                .getSingleResult();

        List<Comment> comments = em.createQuery("select c from Comment c join fetch c.member where c.post.id = :postId and c.parent is null order by c.createdDate", Comment.class)
                .setParameter("postId", postId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(comments, pageable, totalCount);
    }

    public List<Comment> findRepliesByParentId(Long parentId) {
        return em.createQuery("select c from Comment c join fetch c.member where c.parent.id = :parentId order by c.createdDate", Comment.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }

    public long countParentCommentsByPostId(Long postId) {
        return em.createQuery("select count(c) from Comment c where c.post.id = :postId and c.parent is null", Long.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }

    public Optional<Comment> findById(Long commentId) {
        return Optional.ofNullable(em.find(Comment.class, commentId));
    }

    public List<Comment> findByMemberId(Long memberId) {
        return em.createQuery("select c from Comment c where c.member.id = :memberId", Comment.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
