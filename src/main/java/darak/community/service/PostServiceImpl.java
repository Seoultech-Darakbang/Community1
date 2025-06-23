package darak.community.service;

import darak.community.domain.post.Attachment;
import darak.community.domain.log.DeleteLog;
import darak.community.domain.post.Post;
import darak.community.domain.post.PostType;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.repository.CommentRepository;
import darak.community.repository.DeleteLogRepository;
import darak.community.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final DeleteLogRepository deleteLogRepository;

    @Override
    @Transactional
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    @Override
    @Transactional
    public void deleteById(Long id, Member member) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글"));
        if (!hasAuthorization(member, post)) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }
        DeleteLog deleteLog = DeleteLog.postDeleteLog(post, member);
        deleteLogRepository.save(deleteLog);
        postRepository.delete(post);
    }

    private boolean hasAuthorization(Member member, Post post) {
        return post.getMember().equals(member) || member.getMemberGrade() == MemberGrade.ADMIN
                || member.getMemberGrade() == MemberGrade.MASTER;
    }

    @Override
    public List<Post> findByTitle(String title) {
        return postRepository.findByTitle(title);
    }

    @Override
    public List<Post> findByMemberName(String memberName) {
        return postRepository.findByMemberName(memberName);
    }

    @Override
    @Transactional
    public void increaseReadCount(Long id) {
        postRepository.findById(id).ifPresent(Post::increaseReadCount);
    }

    @Override
    public List<Post> findRecentPostsByCategory(Long categoryId, int limit) {
        return postRepository.findRecentPostsByCategory(categoryId, limit);
    }

    @Override
    public List<Attachment> findRecentGalleryImages(int limit) {
        return postRepository.findRecentGalleryImages(limit);
    }

    @Override
    public List<Post> findByBoardId(Long id) {
        return postRepository.findByBoardId(id);
    }

    @Override
    public Page<Post> findByBoardIdPaged(Long boardId, Pageable pageable) {
        return postRepository.findByBoardIdPaged(boardId, pageable);
    }

    @Override
    @Transactional
    public void editPost(Member member, Long postId, String title, PostType postType, String content,
                         Boolean anonymous) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글"));
        if (!hasAuthorization(member, post)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }
        post.edit(title, content, postType, anonymous);
    }

    @Override
    public List<Post> findRecentPostsByBoardId(Long boardId, int limit) {
        return postRepository.findRecentPostsByBoardId(boardId, limit);
    }

    @Override
    public List<Post> findRecentGalleryPostsWithImages(int limit) {
        return postRepository.findRecentGalleryPostsWithImages(limit);
    }

    @Override
    public long countGalleryBoards() {
        return postRepository.countGalleryBoards();
    }

    @Override
    public long countAttachments() {
        return postRepository.countAttachments();
    }

    @Override
    public long countGalleryAttachments() {
        return postRepository.countGalleryAttachments();
    }
}
