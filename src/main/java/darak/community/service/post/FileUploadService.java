package darak.community.service.post;

import darak.community.domain.post.UploadFile;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    String uploadImage(MultipartFile file);
    
    List<UploadFile> uploadAttachments(List<MultipartFile> files);
} 