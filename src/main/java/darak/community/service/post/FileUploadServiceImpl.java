package darak.community.service.post;

import darak.community.domain.post.UploadFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    public String uploadImage(MultipartFile file) {
        Path uploadPath = Paths.get(uploadDir, "images");
        createDirectories(uploadPath);

        String filename = generateUniqueFilename(getFileExtension(file.getOriginalFilename()));
        Path filePath = uploadPath.resolve(filename);

        copyFile(file, filePath);
        return "/uploads/images/" + filename;
    }

    @Override
    public List<UploadFile> uploadAttachments(List<MultipartFile> files) {
        Path uploadPath = Paths.get(uploadDir, "attachments");
        createDirectories(uploadPath);

        List<UploadFile> uploadFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = generateUniqueFilename(extension);
            Path filePath = uploadPath.resolve(filename);

            copyFile(file, filePath);

            uploadFiles.add(UploadFile.builder()
                    .url("/uploads/attachments/" + filename)
                    .size(file.getSize())
                    .fileType(file.getContentType())
                    .fileName(file.getOriginalFilename())
                    .build());
        }
        return uploadFiles;
    }

    private void copyFile(MultipartFile file, Path filePath) {
        try {
            Files.copy(file.getInputStream(), filePath);
            log.info("파일 업로드 완료: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다: " + filePath, e);
        }
    }

    private void createDirectories(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성에 실패했습니다: " + path, e);
            }
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