package darak.community.service.comment.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReplyCreateServiceRequest {

    private Long memberId;
    private Long postId;
    private Long parentCommentId;
    private String content;
    private boolean isAnonymous;

    @Builder
    private ReplyCreateServiceRequest(Long memberId, Long postId, Long parentCommentId, String content,
                                      boolean isAnonymous) {
        this.memberId = memberId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.isAnonymous = isAnonymous;
    }
}
