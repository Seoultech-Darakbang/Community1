package darak.community.service.post;

import darak.community.domain.heart.PostHeart;
import darak.community.service.post.response.MyPostHeartResponse;
import java.util.List;

public interface PostHeartService {

    MyPostHeartResponse addLike(Long postId, Long memberId);

    MyPostHeartResponse removeLike(Long postId, Long memberId);

    MyPostHeartResponse getLikeStatus(Long postId, Long memberId);

    List<PostHeart> findByMemberId(Long memberId);

    int heartCountInPost(Long postId);

    boolean isLiked(Long postId, Long memberId);
}
