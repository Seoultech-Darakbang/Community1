package darak.community.service;

import darak.community.domain.Comment;
import darak.community.domain.member.Member;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    void save(Comment comment);

    void saveFromPost(Member member, Long postId, String content, boolean anonymous);

    void saveReplyFromPost(Member member, Long postId, Long parentCommentId, String content, boolean anonymous);

    void delete(Long commentId);
    
    void deleteWithPermission(Long commentId, Member member);

    List<Comment> findByPostId(Long postId);

    List<Comment> findByMemberId(Long memberId);
    
    Map<Comment, List<Comment>> findCommentsWithReplies(Long postId);
    
    Map<Comment, List<Comment>> findCommentsWithReplies(Long postId, Pageable pageable);
    
    Page<Comment> findParentCommentsByPostId(Long postId, Pageable pageable);
}
