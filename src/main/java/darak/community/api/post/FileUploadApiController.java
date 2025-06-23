package darak.community.api.post;

import darak.community.dto.ApiResponse;
import darak.community.service.FileUploadService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Slf4j
public class FileUploadApiController {

    private final FileUploadService fileUploadService;

    @PostMapping("/images")
    public ApiResponse<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("이미지 업로드 시작 - 파일명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기가 5MB를 초과할 수 없습니다.");
        }

        String imageUrl = fileUploadService.uploadImage(file);
        log.info("이미지 업로드 성공: {}", imageUrl);

        return ApiResponse.success("이미지 업로드에 성공했습니다.",
                new ImageUploadResponse(imageUrl));
    }

    @GetMapping("/test")
    public ApiResponse<ServiceStatusResponse> testUpload() {
        return ApiResponse.success("업로드 서비스가 정상 작동 중입니다.",
                new ServiceStatusResponse("Upload service is running"));
    }

    @Data
    @AllArgsConstructor
    public static class ImageUploadResponse {
        private String url;
    }

    @Data
    @AllArgsConstructor
    public static class ServiceStatusResponse {
        private String status;

    }
}