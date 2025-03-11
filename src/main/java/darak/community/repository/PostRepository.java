package darak.community.repository;

import darak.community.domain.Post;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
}
