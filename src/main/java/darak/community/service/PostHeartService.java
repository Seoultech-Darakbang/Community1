package darak.community.service;

import darak.community.domain.heart.PostHeart;
import java.util.List;

public interface PostHeartService {

    void save(PostHeart postHeart);

    void cancel(Long postId, Long memberId);

    List<PostHeart> findByMemberId(Long memberId);

    int heartCountInPost(Long postId);
}
