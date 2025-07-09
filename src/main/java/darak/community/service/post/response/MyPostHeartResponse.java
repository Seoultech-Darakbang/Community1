package darak.community.service.post.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPostHeartResponse {

    private boolean isLiked;
    private int likeCount;

    public static MyPostHeartResponse of(boolean isLiked, int likeCount) {
        return new MyPostHeartResponse(isLiked, likeCount);
    }
} 