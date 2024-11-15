package darak.community.service;

import darak.community.domain.heart.PostHeart;
import darak.community.repository.PostHeartRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostHeartServiceImpl implements PostHeartService {

    private final PostHeartRepository postHeartRepository;

    @Override
    public void save(PostHeart postHeart) {
        validateDuplicatePostHeart(postHeart);
        postHeartRepository.save(postHeart);
    }

    private void validateDuplicatePostHeart(PostHeart postHeart) {
        if (postHeartRepository.findByPostIdAndMemberId(postHeart.getPost().getId(),
                postHeart.getMember().getId()).isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }
    }

    @Override
    public void cancel(Long postId, Long memberId) {
        PostHeart postHeart = postHeartRepository.findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글에 좋아요를 누르지 않았습니다."));
        postHeartRepository.delete(postHeart);
    }

    @Override
    public List<PostHeart> findByMemberId(Long memberId) {
        return postHeartRepository.findByMemberId(memberId);
    }

    @Override
    public int heartCountInPost(Long postId) {
        return postHeartRepository.countByPostId(postId);
    }
}
