package darak.community.api;

import darak.community.domain.member.Member;
import darak.community.dto.ApiResponse;
import darak.community.dto.LikeResponse;
import darak.community.service.PostHeartService;
import darak.community.service.PostService;
import darak.community.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final PostService postService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> likePost(@Login Member member, @PathVariable Long postId) {
        if (member == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요합니다."));
        }

        try {
            // 이미 좋아요를 눌렀는지 확인
            boolean isAlreadyLiked = postHeartService.isLiked(postId, member.getId());
            
            if (isAlreadyLiked) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("이미 좋아요를 누른 게시글입니다."));
            }

            // 좋아요 추가
            postHeartService.save(postId, member.getId());
            
            // 현재 좋아요 수 조회
            int likeCount = postHeartService.heartCountInPost(postId);
            
            return ResponseEntity.ok(
                    ApiResponse.success("좋아요가 추가되었습니다.", 
                            LikeResponse.of(true, likeCount))
            );
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 좋아요 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<ApiResponse<LikeResponse>> unlikePost(@Login Member member, @PathVariable Long postId) {
        if (member == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요합니다."));
        }

        try {
            // 좋아요를 눌렀는지 확인
            boolean isLiked = postHeartService.isLiked(postId, member.getId());
            
            if (!isLiked) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("좋아요를 누르지 않은 게시글입니다."));
            }

            // 좋아요 취소
            postHeartService.cancel(postId, member.getId());
            
            // 현재 좋아요 수 조회
            int likeCount = postHeartService.heartCountInPost(postId);
            
            return ResponseEntity.ok(
                    ApiResponse.success("좋아요가 취소되었습니다.", 
                            LikeResponse.of(false, likeCount))
            );
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 좋아요 취소 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }
} 