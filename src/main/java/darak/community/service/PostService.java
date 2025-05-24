package darak.community.service;

import darak.community.domain.Attachment;
import darak.community.domain.Post;
import darak.community.domain.PostType;
import darak.community.domain.member.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    void save(Post post);

    Post findById(Long id);

    void deleteById(Long id, Member member);

    List<Post> findByTitle(String title);

    List<Post> findByMemberName(String memberName);

    void increaseReadCount(Long id);

    List<Post> findRecentPostsByCategory(Long categoryId, int limit);

    List<Attachment> findRecentGalleryImages(int limit);

    List<Post> findByBoardId(Long id);

    Page<Post> findByBoardIdPaged(Long boardId, Pageable pageable);

    void editPost(Member member, Long postId, String title, PostType postType, String content, Boolean anonymous);

    List<Post> findRecentPostsByBoardId(Long boardId, int limit);

    List<Post> findRecentGalleryPostsWithImages(int limit);

    long countGalleryBoards();
    long countAttachments();
    long countGalleryAttachments();
}
