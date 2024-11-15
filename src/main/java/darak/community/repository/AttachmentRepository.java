package darak.community.repository;

import darak.community.domain.Attachment;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttachmentRepository {
    private final EntityManager em;

    public void save(Attachment attachment) {
        em.persist(attachment);
    }

    public void delete(Attachment attachment) {
        em.remove(attachment);
    }

    public List<Attachment> findByPostId(Long postId) {
        return em.createQuery("select a from Attachment a where a.post.id = :postId", Attachment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public Optional<Attachment> findById(Long attachmentId) {
        Attachment attachment = em.find(Attachment.class, attachmentId);
        return Optional.ofNullable(attachment);
    }
}
