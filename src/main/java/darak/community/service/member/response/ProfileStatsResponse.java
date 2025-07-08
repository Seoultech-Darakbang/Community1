package darak.community.service.member.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileStatsResponse {

    private long postCount;
    private long commentCount;
    private long postLikeCount;
    private long commentLikeCount;
    private long receivedLikeCount;

    @Builder
    private ProfileStatsResponse(long postCount, long commentCount, long postLikeCount, long commentLikeCount,
                                 long receivedLikeCount) {
        this.postCount = postCount;
        this.commentCount = commentCount;
        this.postLikeCount = postLikeCount;
        this.commentLikeCount = commentLikeCount;
        this.receivedLikeCount = receivedLikeCount;
    }
}
