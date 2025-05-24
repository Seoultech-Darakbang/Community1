package darak.community.api;

import darak.community.dto.ApiResponse;
import darak.community.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/images")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            log.info("이미지 업로드 시작 - 파일명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize());
            
            if (file.isEmpty()) {
                log.warn("빈 파일 업로드 시도");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "파일이 비어있습니다."));
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("이미지가 아닌 파일 업로드 시도: {}", contentType);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "이미지 파일만 업로드 가능합니다."));
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                log.warn("파일 크기 초과: {} bytes", file.getSize());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "파일 크기가 5MB를 초과할 수 없습니다."));
            }

            String imageUrl = fileUploadService.uploadImage(file);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            
            log.info("이미지 업로드 성공: {}", imageUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("이미지 업로드 실패", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "이미지 업로드에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testUpload() {
        return ResponseEntity.ok(Map.of("status", "Upload service is running"));
    }
} 