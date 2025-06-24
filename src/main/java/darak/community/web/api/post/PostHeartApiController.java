package darak.community.web.api.post;

import darak.community.core.argumentresolver.Login;
import darak.community.domain.member.Member;
import darak.community.dto.ApiResponse;
import darak.community.service.post.PostHeartService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostHeartApiController {

    private final PostHeartService postHeartService;

    @PostMapping("/{postId}/like")
    public ApiResponse<LikeResponse> likePost(@Login Member member, @PathVariable Long postId) {
        boolean isAlreadyLiked = postHeartService.isLiked(postId, member.getId());
        if (isAlreadyLiked) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }

        postHeartService.save(postId, member.getId());
        int likeCount = postHeartService.heartCountInPost(postId);

        return ApiResponse.success("좋아요가 추가되었습니다.",
                new LikeResponse(true, likeCount));
    }

    @PostMapping("/{postId}/unlike")
    public ApiResponse<LikeResponse> unlikePost(@Login Member member, @PathVariable Long postId) {
        boolean isLiked = postHeartService.isLiked(postId, member.getId());
        if (!isLiked) {
            throw new IllegalStateException("좋아요를 누르지 않은 게시글입니다.");
        }

        postHeartService.cancel(postId, member.getId());
        int likeCount = postHeartService.heartCountInPost(postId);

        return ApiResponse.success("좋아요가 취소되었습니다.",
                new LikeResponse(false, likeCount));
    }

    @Data
    @AllArgsConstructor
    public static class LikeResponse {
        private boolean isLiked;
        private int likeCount;
    }
}