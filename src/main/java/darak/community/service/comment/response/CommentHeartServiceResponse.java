package darak.community.service.comment.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentHeartServiceResponse {

    private boolean isLiked;
    private int likeCount;
} 