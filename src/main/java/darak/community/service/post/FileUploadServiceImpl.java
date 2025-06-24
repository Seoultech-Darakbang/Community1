package darak.community.service.post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public String uploadImage(MultipartFile file) throws Exception {
        Path uploadPath = Paths.get(uploadDir, "images");

        createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = generateUniqueFilename(extension);

        Path filePath = uploadPath.resolve(filename);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("이미지 업로드 완료: {}", filePath.toAbsolutePath());

            String imageUrl = "/uploads/images/" + filename;
            log.info("이미지 URL: {}", imageUrl);

            return imageUrl;

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", filePath, e);
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
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