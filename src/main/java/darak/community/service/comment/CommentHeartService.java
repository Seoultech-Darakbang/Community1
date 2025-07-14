package darak.community.service.comment;

import darak.community.service.comment.response.MyCommentHeartResponse;

public interface CommentHeartService {
    MyCommentHeartResponse addLike(Long commentId, Long memberId);

    MyCommentHeartResponse removeLike(Long commentId, Long memberId);

    MyCommentHeartResponse getLikeStatus(Long commentId, Long memberId);

    int heartCountInComment(Long commentId);

    boolean isLiked(Long commentId, Long memberId);
}
