package darak.community.service;

import darak.community.domain.heart.PostHeart;
import java.util.List;

public interface PostHeartService {

    void save(Long postId, Long memberId);

    void cancel(Long postId, Long memberId);

    List<PostHeart> findByMemberId(Long memberId);

    int heartCountInPost(Long postId);

    boolean isLiked(Long postId, Long memberId);
}
