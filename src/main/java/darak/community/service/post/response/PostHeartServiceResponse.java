package darak.community.service.post.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostHeartServiceResponse {

    private boolean isLiked;
    private int likeCount;
} 