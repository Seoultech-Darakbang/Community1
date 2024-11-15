package darak.community.service;

import darak.community.domain.heart.CommentHeart;
import java.util.List;

public interface CommentHeartService {
    void save(CommentHeart commentHeart);

    void cancel(Long commentId, Long memberId);

    List<CommentHeart> findByMemberId(Long memberId);

    int heartCountInComment(Long commentId);
}
