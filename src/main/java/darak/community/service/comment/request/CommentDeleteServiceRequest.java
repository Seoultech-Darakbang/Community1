package darak.community.service.comment.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDeleteServiceRequest {

    private Long commentId;
    private Long memberId;
    private String reason;


}
