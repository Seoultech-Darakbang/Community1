package darak.community.repository;

import darak.community.domain.heart.PostHeart;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
}
