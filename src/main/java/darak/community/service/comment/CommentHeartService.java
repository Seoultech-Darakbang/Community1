package darak.community.service.comment;

import darak.community.domain.heart.CommentHeart;
import java.util.List;

public interface CommentHeartService {
    void save(Long commentId, Long memberId);

    void cancel(Long commentId, Long memberId);

    List<CommentHeart> findByMemberId(Long memberId);

    int heartCountInComment(Long commentId);

    boolean isLiked(Long commentId, Long memberId);
}
