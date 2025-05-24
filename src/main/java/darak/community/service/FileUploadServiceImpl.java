package darak.community.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Override
    public String uploadImage(MultipartFile file) throws Exception {
        String staticPath = getStaticUploadsPath();
        Path uploadDir = Paths.get(staticPath, "images");
        
        createDirectories(uploadDir);

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = generateUniqueFilename(extension);

        Path filePath = uploadDir.resolve(filename);
        
        try {
            file.transferTo(filePath.toFile());
            log.info("이미지 업로드 완료: {}", filePath.toAbsolutePath());
            
            String imageUrl = "/uploads/images/" + filename;
            log.info("이미지 URL: {}", imageUrl);
            
            return imageUrl;
            
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", filePath, e);
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    private String getStaticUploadsPath() {
        try {
            String currentDir = System.getProperty("user.dir");
            String staticPath = currentDir + "/src/main/resources/static/uploads";
            
            Path normalizedPath = Paths.get(staticPath).normalize();
            log.info("정적 리소스 업로드 경로: {}", normalizedPath.toAbsolutePath());
            
            return normalizedPath.toString();
            
        } catch (Exception e) {
            log.error("정적 리소스 경로 설정 실패", e);
            throw new RuntimeException("업로드 경로 설정에 실패했습니다.", e);
        }
    }

    private void createDirectories(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("디렉토리 생성 완료: {}", path.toAbsolutePath());
        } else {
            log.debug("디렉토리가 이미 존재합니다: {}", path.toAbsolutePath());
        }
    }

    private String getFileExtension(String filename) {
        if (StringUtils.hasText(filename) && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg"; // 기본 확장자
    }

    private String generateUniqueFilename(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + extension;
    }
} 