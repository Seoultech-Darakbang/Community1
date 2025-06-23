package darak.community.repository;

import darak.community.domain.comment.Comment;
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
        return em.createQuery(
                        "select c from Comment c join fetch c.member where c.post.id = :postId order by c.createdDate",
                        Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public List<Comment> findParentCommentsByPostId(Long postId) {
        return em.createQuery(
                        "select c from Comment c join fetch c.member where c.post.id = :postId and c.parent is null order by c.createdDate",
                        Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public Page<Comment> findParentCommentsByPostId(Long postId, Pageable pageable) {
        Long totalCount = em.createQuery(
                        "select count(c) from Comment c where c.post.id = :postId and c.parent is null", Long.class)
                .setParameter("postId", postId)
                .getSingleResult();

        List<Comment> comments = em.createQuery(
                        "select c from Comment c join fetch c.member where c.post.id = :postId and c.parent is null order by c.createdDate",
                        Comment.class)
                .setParameter("postId", postId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(comments, pageable, totalCount);
    }

    public List<Comment> findRepliesByParentId(Long parentId) {
        return em.createQuery(
                        "select c from Comment c join fetch c.member where c.parent.id = :parentId order by c.createdDate",
                        Comment.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }

    public long countParentCommentsByPostId(Long postId) {
        return em.createQuery("select count(c) from Comment c where c.post.id = :postId and c.parent is null",
                        Long.class)
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

    public long countByMemberId(Long memberId) {
        return em.createQuery("select count(c) from Comment c where c.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    public long countLikesByMemberId(Long memberId) {
        return em.createQuery(
                        "select count(ch) from CommentHeart ch " +
                                "join ch.comment c " +
                                "where c.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    public Page<Comment> findByMemberIdPaged(Long memberId, Pageable pageable) {
        List<Comment> comments = em.createQuery(
                        "select c from Comment c " +
                                "join fetch c.post p " +
                                "join fetch p.board b " +
                                "where c.member.id = :memberId " +
                                "order by c.createdDate desc", Comment.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery("select count(c) from Comment c where c.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }

    public Page<Comment> findLikedCommentsByMemberId(Long memberId, Pageable pageable) {
        List<Comment> comments = em.createQuery(
                        "select c from Comment c " +
                                "join fetch c.post p " +
                                "join fetch p.board b " +
                                "join CommentHeart ch on ch.comment.id = c.id " +
                                "where ch.member.id = :memberId " +
                                "order by ch.createdDate desc", Comment.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery(
                        "select count(c) from Comment c " +
                                "join CommentHeart ch on ch.comment.id = c.id " +
                                "where ch.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }

    public Page<Comment> searchMyComments(Long memberId, String keyword, String boardName, Pageable pageable) {
        StringBuilder query = new StringBuilder(
                "select c from Comment c " +
                        "join fetch c.post p " +
                        "join fetch p.board b " +
                        "where c.member.id = :memberId");

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append(" and c.content like :keyword");
        }

        if (boardName != null && !boardName.trim().isEmpty()) {
            query.append(" and b.name = :boardName");
        }

        query.append(" order by c.createdDate desc");

        var jpqlQuery = em.createQuery(query.toString(), Comment.class)
                .setParameter("memberId", memberId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            jpqlQuery.setParameter("keyword", "%" + keyword + "%");
        }

        if (boardName != null && !boardName.trim().isEmpty()) {
            jpqlQuery.setParameter("boardName", boardName);
        }

        List<Comment> comments = jpqlQuery
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count query
        StringBuilder countQuery = new StringBuilder(
                "select count(c) from Comment c " +
                        "join c.post p " +
                        "join p.board b " +
                        "where c.member.id = :memberId");

        if (keyword != null && !keyword.trim().isEmpty()) {
            countQuery.append(" and c.content like :keyword");
        }

        if (boardName != null && !boardName.trim().isEmpty()) {
            countQuery.append(" and b.name = :boardName");
        }

        var countJpqlQuery = em.createQuery(countQuery.toString(), Long.class)
                .setParameter("memberId", memberId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            countJpqlQuery.setParameter("keyword", "%" + keyword + "%");
        }

        if (boardName != null && !boardName.trim().isEmpty()) {
            countJpqlQuery.setParameter("boardName", boardName);
        }

        Long count = countJpqlQuery.getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }
}
