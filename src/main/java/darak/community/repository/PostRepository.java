package darak.community.repository;

import darak.community.domain.Attachment;
import darak.community.domain.Post;
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
        List<Post> posts = em.createQuery("select p from Post p where p.board.id = :boardId order by p.createdDate desc", Post.class)
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

    // 디버깅용 메서드 추가
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
}
