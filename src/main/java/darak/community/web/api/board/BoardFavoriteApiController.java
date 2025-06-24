package darak.community.web.api.board;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.dto.ApiResponse;
import darak.community.service.board.BoardFavoriteService;
import darak.community.service.board.response.FavoriteServiceResponse;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class BoardFavoriteApiController {

    private final BoardFavoriteService boardFavoriteService;

    @PostMapping("/{boardId}")
    public ApiResponse<FavoriteResponse> addFavorite(@Login LoginMember loginMember, @PathVariable Long boardId) {
        boardFavoriteService.addFavorite(loginMember.getId(), boardId);

        return ApiResponse.success("즐겨찾기에 추가되었습니다.",
                new FavoriteResponse(true));
    }

    @DeleteMapping("/{boardId}")
    public ApiResponse<FavoriteResponse> removeFavorite(@Login LoginMember loginMember, @PathVariable Long boardId) {
        boardFavoriteService.removeFavorite(loginMember.getId(), boardId);

        return ApiResponse.success("즐겨찾기에서 제거되었습니다.",
                new FavoriteResponse(false));
    }

    @GetMapping("/{boardId}/status")
    public ApiResponse<FavoriteResponse> getFavoriteStatus(@Login LoginMember loginMember, @PathVariable Long boardId) {
        FavoriteServiceResponse favorite = boardFavoriteService.isFavorite(loginMember.getId(), boardId);

        return ApiResponse.success("즐겨찾기 상태 조회에 성공했습니다.",
                FavoriteResponse.from(favorite));
    }

    @Data
    @AllArgsConstructor
    public static class FavoriteResponse {
        private boolean isFavorite;

        public static FavoriteResponse from(FavoriteServiceResponse fsr) {
            return new FavoriteResponse(fsr.isFavorite());
        }
    }
}