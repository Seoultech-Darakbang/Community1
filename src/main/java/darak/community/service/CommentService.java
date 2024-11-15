package darak.community.service;

import darak.community.domain.Comment;
import java.util.List;

public interface CommentService {
    void save(Comment comment);

    void delete(Long commentId);

    List<Comment> findByPostId(Long postId);

    List<Comment> findByMemberId(Long memberId);
}
