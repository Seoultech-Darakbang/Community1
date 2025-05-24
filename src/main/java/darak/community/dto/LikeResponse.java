package darak.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeResponse {
    private boolean liked;
    private int likeCount;
    
    public static LikeResponse of(boolean liked, int likeCount) {
        return new LikeResponse(liked, likeCount);
    }
} 