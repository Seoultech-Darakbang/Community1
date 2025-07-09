package darak.community.service.comment;

import darak.community.service.comment.request.CommentCreateServiceRequest;
import darak.community.service.comment.request.CommentDeleteServiceRequest;
import darak.community.service.comment.request.ReplyCreateServiceRequest;
import darak.community.service.comment.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    void createFromPost(CommentCreateServiceRequest request);

    void createReplyFromPost(ReplyCreateServiceRequest request);

    void deleteCommentBy(Long commentId, Long memberId);

    void deleteCommentByAdmin(CommentDeleteServiceRequest request);

    Page<CommentResponse> findCommentsWithReplyBy(Long memberId, Long postId, Pageable pageable);

    Page<CommentResponse> findCommentsBy(Long memberId, Pageable pageable);

    Page<CommentResponse> findHeartCommentsBy(Long memberId, Pageable pageable);

    long getTotalCommentCount();
}
