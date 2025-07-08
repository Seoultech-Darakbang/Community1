package darak.community.service.comment.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentCreateServiceRequest {

    private Long memberId;
    private Long postId;
    private String content;
    private boolean isAnonymous;

    @Builder
    private CommentCreateServiceRequest(Long memberId, Long postId, String content, boolean isAnonymous) {
        this.memberId = memberId;
        this.postId = postId;
        this.content = content;
        this.isAnonymous = isAnonymous;
    }
}
