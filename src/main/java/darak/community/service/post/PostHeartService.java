package darak.community.service.post;

import darak.community.domain.heart.PostHeart;
import darak.community.service.post.response.PostHeartServiceResponse;
import java.util.List;

public interface PostHeartService {

    PostHeartServiceResponse addLike(Long postId, Long memberId);

    PostHeartServiceResponse removeLike(Long postId, Long memberId);

    PostHeartServiceResponse getLikeStatus(Long postId, Long memberId);

    List<PostHeart> findByMemberId(Long memberId);

    int heartCountInPost(Long postId);

    boolean isLiked(Long postId, Long memberId);
}
