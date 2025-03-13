package darak.community.service;

import darak.community.domain.Post;
import darak.community.domain.heart.PostHeart;
import darak.community.domain.member.Member;
import darak.community.repository.MemberRepository;
import darak.community.repository.PostHeartRepository;
import darak.community.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostHeartServiceImpl implements PostHeartService {

    private final PostHeartRepository postHeartRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void save(Long postId, Long memberId) {
        if (postHeartRepository.findByPostIdAndMemberId(postId, memberId).isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // PostHeart 생성 및 저장 (양방향 관계 설정은 생성자에서 처리)
        PostHeart postHeart = new PostHeart(post, member);
        postHeartRepository.save(postHeart);
    }

    @Override
    @Transactional
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

    @Override
    public boolean isLiked(Long postId, Long memberId) {
        return postHeartRepository.findByPostIdAndMemberId(postId, memberId).isPresent();
    }
}
