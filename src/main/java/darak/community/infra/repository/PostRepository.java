package darak.community.infra.repository;

import darak.community.domain.post.Attachment;
import darak.community.domain.post.Post;
import darak.community.infra.repository.dto.PostWithMetaDto;
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
public class PostRepository {
    private final EntityManager em;

    public void save(Post post) {
        em.persist(post);
    }

    public Optional<Post> findById(Long id) {
        Post post = em.find(Post.class, id);
        return Optional.ofNullable(post);
    }

    public List<Post> findByTitle(String title) {
        return em.createQuery("select p from Post p where p.title = :title", Post.class)
                .setParameter("title", title)
                .getResultList();
    }

    public List<Post> findByMemberName(String memberName) {
        return em.createQuery("select p from Post p where p.member.name = :memberName", Post.class)
                .setParameter("memberName", memberName)
                .getResultList();
    }

    public void delete(Post post) {
        em.remove(post);
    }

    public void deleteById(Long id) {
        em.createQuery("delete from Post p where p.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<Post> findRecentPostsByCategory(Long categoryId, int limit) {
        return em.createQuery(
                        "select p from Post p join p.board b where b.boardCategory.id = :categoryId " +
                                "order by p.createdDate desc", Post.class)
                .setParameter("categoryId", categoryId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Attachment> findRecentGalleryImages(int limit) {
        return em.createQuery(
                        "select a from Attachment a " +
                                "join a.post p " +
                                "join p.board b " +
                                "where (lower(b.name) like '%갤러리%' or lower(b.name) like '%gallery%') " +
                                "and a.fileType like 'image/%' " +
                                "order by p.createdDate desc", Attachment.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Post> findByBoardId(Long id) {
        return em.createQuery("select p from Post p where p.board.id = :id", Post.class)
                .setParameter("id", id)
                .getResultList();
    }

    public Page<Post> findByBoardIdPaged(Long boardId, Pageable pageable) {
        List<Post> posts = em.createQuery(
                        "select p from Post p where p.board.id = :boardId order by p.createdDate desc", Post.class)
                .setParameter("boardId", boardId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery("select count(p) from Post p where p.board.id = :boardId", Long.class)
                .setParameter("boardId", boardId)
                .getSingleResult();

        return new PageImpl<>(posts, pageable, count);
    }

    public List<Post> findRecentPostsByBoardId(Long boardId, int limit) {
        return em.createQuery(
                        "select p from Post p where p.board.id = :boardId " +
                                "order by p.createdDate desc", Post.class)
                .setParameter("boardId", boardId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Post> findRecentGalleryPostsWithImages(int limit) {
        return em.createQuery(
                        "select distinct p from Post p " +
                                "join p.board b " +
                                "join p.attachments a " +
                                "where (lower(b.name) like '%갤러리%' or lower(b.name) like '%gallery%') " +
                                "and a.fileType like 'image/%' " +
                                "order by p.createdDate desc", Post.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public long countGalleryBoards() {
        return em.createQuery(
                        "select count(b) from Board b " +
                                "where (lower(b.name) like '%갤러리%' or lower(b.name) like '%gallery%')",
                        Long.class)
                .getSingleResult();
    }

    public long countAttachments() {
        return em.createQuery("select count(a) from Attachment a", Long.class)
                .getSingleResult();
    }

    public long countGalleryAttachments() {
        return em.createQuery(
                        "select count(a) from Attachment a " +
                                "join a.post p " +
                                "join p.board b " +
                                "where (lower(b.name) like '%갤러리%' or lower(b.name) like '%gallery%') " +
                                "and a.fileType like 'image/%'",
                        Long.class)
                .getSingleResult();
    }

    public long countByMemberId(Long memberId) {
        return em.createQuery("select count(p) from Post p where p.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    public long countLikesByMemberId(Long memberId) {
        return em.createQuery(
                        "select count(ph) from PostHeart ph " +
                                "join ph.post p " +
                                "where p.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    public Page<Post> findByMemberIdPaged(Long memberId, Pageable pageable) {
        List<Post> posts = em.createQuery(
                        "select p from Post p " +
                                "where p.member.id = :memberId " +
                                "order by p.createdDate desc", Post.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery("select count(p) from Post p where p.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(posts, pageable, count);
    }

    public Page<Post> findLikedPostsByMemberId(Long memberId, Pageable pageable) {
        List<Post> posts = em.createQuery(
                        "select p from Post p " +
                                "join PostHeart ph on ph.post.id = p.id " +
                                "where ph.member.id = :memberId " +
                                "order by ph.createdDate desc", Post.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery(
                        "select count(p) from Post p " +
                                "join PostHeart ph on ph.post.id = p.id " +
                                "where ph.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(posts, pageable, count);
    }

    public Page<Post> searchMyPosts(Long memberId, String keyword, String boardName, Pageable pageable) {
        StringBuilder query = new StringBuilder(
                "select p from Post p " +
                        "join p.board b " +
                        "where p.member.id = :memberId");

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append(" and (p.title like :keyword or p.content like :keyword)");
        }

        if (boardName != null && !boardName.trim().isEmpty()) {
            query.append(" and b.name = :boardName");
        }

        query.append(" order by p.createdDate desc");

        var jpqlQuery = em.createQuery(query.toString(), Post.class)
                .setParameter("memberId", memberId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            jpqlQuery.setParameter("keyword", "%" + keyword + "%");
        }

        if (boardName != null && !boardName.trim().isEmpty()) {
            jpqlQuery.setParameter("boardName", boardName);
        }

        List<Post> posts = jpqlQuery
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count query
        StringBuilder countQuery = new StringBuilder(
                "select count(p) from Post p " +
                        "join p.board b " +
                        "where p.member.id = :memberId");

        if (keyword != null && !keyword.trim().isEmpty()) {
            countQuery.append(" and (p.title like :keyword or p.content like :keyword)");
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

        return new PageImpl<>(posts, pageable, count);
    }

    public Page<PostWithMetaDto> findPostsWithMetaByMemberId(Long memberId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.repository.dto.PostWithMetaDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade, b.id, b.name,
                            p.readCount, p.createdDate,
                            (select count(c) from Comment c
                             where c.post = p),
                            case when
                                 (select count(ph2) from PostHeart ph2
                                  where ph2.post = p
                                    and ph2.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            (select count(ph) from PostHeart ph
                             where ph.post = p)
                        )
                        from Post p
                        join p.member m
                        join p.board b
                        where p.member.id = :memberId
                        order by p.createdDate desc
                """;
        String countJpql = """
                select count(p)
                from Post p
                where p.member.id = :memberId
                """;

        List<PostWithMetaDto> content = em.createQuery(jpql, PostWithMetaDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<PostWithMetaDto> findPostsWithMetaByMemberLiked(Long memberId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.repository.dto.PostWithMetaDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade, b.id, b.name,
                            p.readCount, p.createdDate,
                            (select count(c) from Comment c
                             where c.post = p),
                            case when
                                 (select count(ph2) from PostHeart ph2
                                  where ph2.post = p
                                    and ph2.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            (select count(ph) from PostHeart ph
                             where ph.post = p)
                        )
                        from Post p
                        join p.member m
                        join p.board b
                        join PostHeart ph on ph.post.id = p.id
                        where ph.member.id = :memberId
                        order by p.createdDate desc
                """;
        String countJpql = """
                select count(p)
                from Post p
                where p.member.id = :memberId
                """;

        List<PostWithMetaDto> content = em.createQuery(jpql, PostWithMetaDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public long count() {
        return em.createQuery("select count(p) from Post p", Long.class)
                .getSingleResult();
    }

}
