package darak.community.web.api.post;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.service.post.PostHeartService;
import darak.community.service.post.response.MyPostHeartResponse;
import darak.community.web.dto.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ApiResponse<LikeResponse> addLike(@Login LoginMember loginMember, @PathVariable Long postId) {
        MyPostHeartResponse response = postHeartService.addLike(postId, loginMember.getId());

        return ApiResponse.success("좋아요가 추가되었습니다.",
                LikeResponse.from(response));
    }

    @DeleteMapping("/{postId}/like")
    public ApiResponse<LikeResponse> removeLike(@Login LoginMember loginMember, @PathVariable Long postId) {
        MyPostHeartResponse response = postHeartService.removeLike(postId, loginMember.getId());

        return ApiResponse.success("좋아요가 취소되었습니다.",
                LikeResponse.from(response));
    }

    @GetMapping("/{postId}/like/status")
    public ApiResponse<LikeResponse> getLikeStatus(@Login LoginMember loginMember, @PathVariable Long postId) {
        MyPostHeartResponse response = postHeartService.getLikeStatus(postId, loginMember.getId());

        return ApiResponse.success("좋아요 상태 조회에 성공했습니다.",
                LikeResponse.from(response));
    }

    @Data
    @AllArgsConstructor
    public static class LikeResponse {
        private boolean isLiked;
        private int likeCount;

        public static LikeResponse from(MyPostHeartResponse response) {
            return new LikeResponse(response.isLiked(), response.getLikeCount());
        }
    }
}