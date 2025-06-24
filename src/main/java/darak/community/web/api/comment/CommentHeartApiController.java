package darak.community.web.api.comment;

import darak.community.core.argumentresolver.Login;
import darak.community.domain.member.Member;
import darak.community.dto.ApiResponse;
import darak.community.service.CommentHeartService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ApiResponse<LikeResponse> toggleCommentLike(@Login Member member, @PathVariable Long commentId) {
        boolean isCurrentlyLiked = commentHeartService.isLiked(commentId, member.getId());

        if (isCurrentlyLiked) {
            commentHeartService.cancel(commentId, member.getId());
            int likeCount = commentHeartService.heartCountInComment(commentId);

            return ApiResponse.success("좋아요가 취소되었습니다.",
                    new LikeResponse(false, likeCount));
        }

        commentHeartService.save(commentId, member.getId());
        int likeCount = commentHeartService.heartCountInComment(commentId);

        return ApiResponse.success("좋아요가 추가되었습니다.",
                new LikeResponse(true, likeCount));
    }

    @Data
    @AllArgsConstructor
    public static class LikeResponse {
        private boolean isLiked;
        private int likeCount;
    }
}