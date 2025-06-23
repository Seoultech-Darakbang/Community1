package darak.community.repository;

import darak.community.domain.post.Post;
import darak.community.domain.heart.PostHeart;
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
public class PostHeartRepository {

    private final EntityManager em;

    public void save(PostHeart postHeart) {
        em.persist(postHeart);
    }

    public void delete(PostHeart postHeart) {
        em.remove(postHeart);
    }

    public int countByPostId(Long postId) {
        return em.createQuery("select count(ph) from PostHeart ph where ph.post.id = :postId", Long.class)
                .setParameter("postId", postId)
                .getSingleResult()
                .intValue();
    }

    public List<PostHeart> findByMemberId(Long memberId) {
        return em.createQuery("select ph from PostHeart ph where ph.member.id = :memberId", PostHeart.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public Optional<PostHeart> findByPostIdAndMemberId(Long postId, Long memberId) {
        List<PostHeart> result = em.createQuery(
                        "select ph from PostHeart ph where ph.post.id = :postId and ph.member.id = :memberId",
                        PostHeart.class)
                .setParameter("postId", postId)
                .setParameter("memberId", memberId)
                .getResultList();
        return result.stream().findAny();
    }

    public Page<Post> findLikedPostsByMember(Long memberId, Pageable pageable) {
        // 좋아요한 게시글 조회 쿼리
        String jpql = "SELECT p FROM PostHeart ph " +
                "JOIN ph.post p " +
                "WHERE ph.member.id = :memberId " +
                "ORDER BY ph.createdDate DESC";

        TypedQuery<Post> query = em.createQuery(jpql, Post.class)
                .setParameter("memberId", memberId);

        // 전체 개수 조회
        String countJpql = "SELECT COUNT(ph) FROM PostHeart ph WHERE ph.member.id = :memberId";
        Long totalCount = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        query.setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<Post> posts = query.getResultList();

        return new PageImpl<>(posts, pageable, totalCount);
    }
}
