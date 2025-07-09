package darak.community.service.post.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class AttachmentCreateServiceRequest {

    private Long postId;
    private List<MultipartFile> files;

    public static AttachmentCreateServiceRequest of(Long postId, List<MultipartFile> files) {
        return new AttachmentCreateServiceRequest(postId, files);
    }

}
