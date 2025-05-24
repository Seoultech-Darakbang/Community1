package darak.community.api;

import darak.community.domain.member.Member;
import darak.community.dto.ApiResponse;
import darak.community.dto.LikeResponse;
import darak.community.service.CommentHeartService;
import darak.community.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<LikeResponse>> toggleCommentLike(@Login Member member, @PathVariable Long commentId) {
        if (member == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요합니다."));
        }

        try {
            // 현재 좋아요 상태 확인
            boolean isCurrentlyLiked = commentHeartService.isLiked(commentId, member.getId());
            
            if (isCurrentlyLiked) {
                // 좋아요 취소
                commentHeartService.cancel(commentId, member.getId());
                int likeCount = commentHeartService.heartCountInComment(commentId);
                
                return ResponseEntity.ok(
                        ApiResponse.success("좋아요가 취소되었습니다.", 
                                LikeResponse.of(false, likeCount))
                );
            } else {
                // 좋아요 추가
                commentHeartService.save(commentId, member.getId());
                int likeCount = commentHeartService.heartCountInComment(commentId);
                
                return ResponseEntity.ok(
                        ApiResponse.success("좋아요가 추가되었습니다.", 
                                LikeResponse.of(true, likeCount))
                );
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            // 이미 좋아요를 누른 경우 등의 상태 오류
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("댓글 좋아요 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }
} 