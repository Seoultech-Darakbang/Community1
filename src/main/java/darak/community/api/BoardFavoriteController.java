package darak.community.api;

import darak.community.domain.Board;
import darak.community.domain.member.Member;
import darak.community.dto.ApiResponse;
import darak.community.service.BoardFavoriteService;
import darak.community.service.BoardService;
import darak.community.web.argumentresolver.Login;
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
public class BoardFavoriteController {

    private final BoardFavoriteService boardFavoriteService;
    private final BoardService boardService;

    @PostMapping("/{boardId}")
    public ApiResponse<FavoriteResponse> addFavorite(@Login Member member, @PathVariable Long boardId) {
        Board board = boardService.findById(boardId);
        boardFavoriteService.addFavorite(member, board);

        return ApiResponse.success("즐겨찾기에 추가되었습니다.",
                new FavoriteResponse(true));
    }

    @DeleteMapping("/{boardId}")
    public ApiResponse<FavoriteResponse> removeFavorite(@Login Member member, @PathVariable Long boardId) {
        boardFavoriteService.removeFavorite(member.getId(), boardId);

        return ApiResponse.success("즐겨찾기에서 제거되었습니다.",
                new FavoriteResponse(false));
    }

    @GetMapping("/{boardId}/status")
    public ApiResponse<FavoriteResponse> getFavoriteStatus(@Login Member member, @PathVariable Long boardId) {
        boolean isFavorite = boardFavoriteService.isFavorite(member.getId(), boardId);

        return ApiResponse.success("즐겨찾기 상태 조회에 성공했습니다.",
                new FavoriteResponse(isFavorite));
    }

    @Data
    @AllArgsConstructor
    public static class FavoriteResponse {
        private boolean isFavorite;
    }
}