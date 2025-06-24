package darak.community.service.comment;

import darak.community.service.comment.response.CommentHeartServiceResponse;

public interface CommentHeartService {
    CommentHeartServiceResponse addLike(Long commentId, Long memberId);

    CommentHeartServiceResponse removeLike(Long commentId, Long memberId);

    CommentHeartServiceResponse getLikeStatus(Long commentId, Long memberId);

    int heartCountInComment(Long commentId);

    boolean isLiked(Long commentId, Long memberId);
}
