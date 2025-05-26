package darak.community.service;

import darak.community.domain.Comment;
import darak.community.domain.heart.CommentHeart;
import darak.community.domain.member.Member;
import darak.community.repository.CommentHeartRepository;
import darak.community.repository.CommentRepository;
import darak.community.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentHeartServiceImpl implements CommentHeartService {

    private final CommentHeartRepository commentHeartRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void save(Long commentId, Long memberId) {
        if (commentHeartRepository.findByCommentIdAndMemberId(commentId, memberId).isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누른 댓글입니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        CommentHeart commentHeart = new CommentHeart(comment, member);
        commentHeartRepository.save(commentHeart);
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

    @Override
    public boolean isLiked(Long commentId, Long memberId) {
        return commentHeartRepository.findByCommentIdAndMemberId(commentId, memberId).isPresent();
    }
}
