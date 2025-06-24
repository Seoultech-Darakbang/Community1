package darak.community.web.api.comment;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.dto.ApiResponse;
import darak.community.service.comment.CommentHeartService;
import darak.community.service.comment.response.CommentHeartServiceResponse;
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
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentHeartApiController {

    private final CommentHeartService commentHeartService;

    @PostMapping("/{commentId}/like")
    public ApiResponse<LikeResponse> addLike(@Login LoginMember loginMember, @PathVariable Long commentId) {
        CommentHeartServiceResponse response = commentHeartService.addLike(commentId, loginMember.getId());

        return ApiResponse.success("좋아요가 추가되었습니다.",
                LikeResponse.from(response));
    }

    @DeleteMapping("/{commentId}/like")
    public ApiResponse<LikeResponse> removeLike(@Login LoginMember loginMember, @PathVariable Long commentId) {
        CommentHeartServiceResponse response = commentHeartService.removeLike(commentId, loginMember.getId());

        return ApiResponse.success("좋아요가 취소되었습니다.",
                LikeResponse.from(response));
    }

    @GetMapping("/{commentId}/like/status")
    public ApiResponse<LikeResponse> getLikeStatus(@Login LoginMember loginMember, @PathVariable Long commentId) {
        CommentHeartServiceResponse response = commentHeartService.getLikeStatus(commentId, loginMember.getId());

        return ApiResponse.success("좋아요 상태 조회에 성공했습니다.",
                LikeResponse.from(response));
    }

    @Data
    @AllArgsConstructor
    public static class LikeResponse {
        private boolean isLiked;
        private int likeCount;

        public static LikeResponse from(CommentHeartServiceResponse response) {
            return new LikeResponse(response.isLiked(), response.getLikeCount());
        }
    }
}