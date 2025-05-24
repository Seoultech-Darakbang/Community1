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
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (member == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(401).body(response);
            }

            Board board = boardService.findById(boardId);
            boardFavoriteService.addFavorite(member, board);

            response.put("success", true);
            response.put("isFavorite", true);
            response.put("message", "즐겨찾기에 추가되었습니다.");
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("즐겨찾기 추가 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "즐겨찾기 추가 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(@Login Member member, @PathVariable Long boardId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (member == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(401).body(response);
            }

            boardFavoriteService.removeFavorite(member.getId(), boardId);

            response.put("success", true);
            response.put("isFavorite", false);
            response.put("message", "즐겨찾기에서 제거되었습니다.");
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("즐겨찾기 제거 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "즐겨찾기 제거 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardId}/status")
    public ResponseEntity<Map<String, Object>> getFavoriteStatus(@Login Member member, @PathVariable Long boardId) {
        Map<String, Object> response = new HashMap<>();
        
        if (member == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            boolean isFavorite = boardFavoriteService.isFavorite(member.getId(), boardId);
            response.put("success", true);
            response.put("isFavorite", isFavorite);
        } catch (Exception e) {
            log.error("즐겨찾기 상태 조회 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "즐겨찾기 상태 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
