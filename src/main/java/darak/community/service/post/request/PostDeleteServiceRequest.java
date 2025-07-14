package darak.community.service.post.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostDeleteServiceRequest {

    private Long postId;
    private Long memberId;
    private String reason;

}
