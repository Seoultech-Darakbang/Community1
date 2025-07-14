package darak.community.infra.repository;

import darak.community.domain.comment.Comment;
import darak.community.infra.repository.dto.CommentInPostDto;
import darak.community.infra.repository.dto.CommentWithMetaDto;
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

    public List<Comment> findByPostId(Long postId, Pageable pageable) {
        return em.createQuery(
                        "select c from Comment c join fetch c.member where c.post.id = :postId order by c.createdDate",
                        Comment.class)
                .setParameter("postId", postId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public List<Comment> findParentCommentsByPostIdPaged(Long postId) {
        return em.createQuery(
                        "select c from Comment c join fetch c.member where c.post.id = :postId and c.parent is null order by c.createdDate",
                        Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public Page<Comment> findParentCommentsByPostIdPaged(Long postId, Pageable pageable) {
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

    public List<Comment> findChildCommentsByParentIds(List<Long> parentCommentIds) {
        String jpql = """
                select c from Comment c
                join fetch c.member
                where c.parent.id in :parentCommentIds
                """;

        return em.createQuery(jpql, Comment.class)
                .setParameter("parentCommentIds", parentCommentIds)
                .getResultList();
    }

    public long count() {
        return em.createQuery("select count(c) from Comment c", Long.class)
                .getSingleResult();
    }

    public Page<CommentWithMetaDto> findCommentsWithMetaByMemberIdAndPostIdPaged(Long memberId, Long postId,
                                                                                 Pageable pageable) {
        String jpql = """
                select new darak.community.infra.repository.dto.CommentWithMetaDto(
                    c.id, m.id, c.anonymous, m.name, c.content,
                    case when c.parent is not null then true else false end,
                    p.id, p.title, p.postType,
                    b.id, b.name, c.createdDate,
                    case when
                        (select count(ch) from CommentHeart ch where ch.comment.id = c.id and ch.member.id = :memberId) > 0
                        then true else false end,
                    cast((select count(ch) from CommentHeart ch where ch.comment.id = c.id) as int)
                )
                from Comment c
                join c.member m
                join c.post p
                join p.board b
                where c.post.id = :postId
                order by c.createdDate desc
                """;

        String countJpql = """
                select count(c)
                from Comment c
                where c.post.id = :postId
                """;

        List<CommentWithMetaDto> comments = em.createQuery(jpql, CommentWithMetaDto.class)
                .setParameter("memberId", memberId)
                .setParameter("postId", postId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery(countJpql, Long.class)
                .setParameter("postId", postId)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }

    public Page<CommentWithMetaDto> findCommentsWithMetaByMemberIdPaged(Long memberId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.repository.dto.CommentWithMetaDto(
                    c.id, m.id, c.anonymous, m.name, c.content,
                    case when c.parent is not null then true else false end,
                    p.id, p.title, p.postType,
                    b.id, b.name, c.createdDate,
                    case when
                        (select count(ch) from CommentHeart ch where ch.comment.id = c.id and ch.member.id = :memberId) > 0
                        then true else false end,
                    cast((select count(ch) from CommentHeart ch where ch.comment.id = c.id) as int)
                )
                from Comment c
                join c.member m
                join c.post p
                join p.board b
                where c.member.id = :memberId
                order by c.createdDate desc
                """;

        String countJpql = """
                select count(c)
                from Comment c
                where c.member.id = :memberId
                """;

        List<CommentWithMetaDto> comments = em.createQuery(jpql, CommentWithMetaDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }

    public Page<CommentWithMetaDto> findCommentsWithMetaByMemberLiked(Long memberId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.repository.dto.CommentWithMetaDto(
                    c.id, m.id, c.anonymous, m.name, c.content,
                    case when c.parent is not null then true else false end,
                    p.id, p.title, p.postType,
                    b.id, b.name, c.createdDate,
                    true,
                    cast((select count(ch2) from CommentHeart ch2 where ch2.comment.id = c.id) as int)
                )
                from CommentHeart ch
                join ch.comment c
                join c.member m
                join c.post p
                join p.board b
                where ch.member.id = :memberId
                order by ch.createdDate desc
                """;

        String countJpql = """
                select count(c)
                from CommentHeart ch
                join ch.comment c
                where ch.member.id = :memberId
                """;

        List<CommentWithMetaDto> comments = em.createQuery(jpql, CommentWithMetaDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }

    public Page<CommentInPostDto> findCommentInPostByPostIdAndMemberIdPaged(Long postId, Long memberId,
                                                                            Pageable pageable) {
        String jpql = """
                select new darak.community.infra.repository.dto.CommentInPostDto(
                    c.id, m.id, c.anonymous, m.name, c.content,
                    case when c.parent is not null then true else false end,
                    c.createdDate,
                    case when
                        (select count(ch) from CommentHeart ch where ch.comment.id = c.id and ch.member.id = :memberId) > 0
                        then true else false end,
                    cast((select count(ch) from CommentHeart ch where ch.comment.id = c.id) as int)
                )
                from Comment c
                join c.member m
                where c.post.id = :postId
                order by c.createdDate desc
                """;

        String countJpql = """
                select count(c)
                from Comment c
                where c.post.id = :postId
                """;

        List<CommentInPostDto> comments = em.createQuery(jpql, CommentInPostDto.class)
                .setParameter("memberId", memberId)
                .setParameter("postId", postId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery(countJpql, Long.class)
                .setParameter("postId", postId)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }
}
