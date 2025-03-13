package darak.community.api;

import darak.community.domain.Board;
import darak.community.domain.member.Member;
import darak.community.service.BoardFavoriteService;
import darak.community.service.BoardService;
import darak.community.web.argumentresolver.Login;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    // TODO: 테스트 코드 작성하고, 뷰, AJAX나 fetch 코드 작성해야 함

    @PostMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> addFavorite(@Login Member member, @PathVariable Long boardId) {
        if (member == null) {
            return ResponseEntity.badRequest().build();
        }

        Board board = boardService.findById(boardId);
        boardFavoriteService.addFavorite(member, board);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isFavorite", true);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(@Login Member member, @PathVariable Long boardId) {
        if (member == null) {
            return ResponseEntity.badRequest().build();
        }

        boardFavoriteService.removeFavorite(member.getId(), boardId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isFavorite", false);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardId}/status")
    public ResponseEntity<Map<String, Object>> getFavoriteStatus(@Login Member member, @PathVariable Long boardId) {
        if (member == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean isFavorite = boardFavoriteService.isFavorite(member.getId(), boardId);

        Map<String, Object> response = new HashMap<>();
        response.put("isFavorite", isFavorite);

        return ResponseEntity.ok(response);
    }
}
