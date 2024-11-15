package darak.community.service;

import darak.community.domain.heart.CommentHeart;
import darak.community.repository.CommentHeartRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentHeartServiceImpl implements CommentHeartService {

    private final CommentHeartRepository commentHeartRepository;

    @Override
    @Transactional
    public void save(CommentHeart commentHeart) {
        validateDuplicateCommentHeart(commentHeart);
        commentHeartRepository.save(commentHeart);
    }

    private void validateDuplicateCommentHeart(CommentHeart commentHeart) {
        if (commentHeartRepository.findByCommentIdAndMemberId(commentHeart.getComment().getId(),
                commentHeart.getMember().getId()).isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누른 댓글입니다.");
        }
    }

    @Override
    @Transactional
    public void cancel(Long commentId, Long memberId) {
        CommentHeart commentHeart = commentHeartRepository.findByCommentIdAndMemberId(commentId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글에 좋아요를 누르지 않았습니다."));
        commentHeartRepository.delete(commentHeart);
    }

    @Override
    public List<CommentHeart> findByMemberId(Long memberId) {
        return commentHeartRepository.findByMemberId(memberId);
    }

    @Override
    public int heartCountInComment(Long commentId) {
        return commentHeartRepository.countByCommentId(commentId);
    }
}
