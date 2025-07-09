package darak.community.service.post;

import darak.community.service.post.request.AttachmentCreateServiceRequest;
import darak.community.service.post.response.AttachmentResponse;
import java.util.List;

public interface AttachmentService {

    List<AttachmentResponse> addAttachments(AttachmentCreateServiceRequest request);

    void delete(Long attachmentId);

    List<AttachmentResponse> findAttachmentsByPostId(Long postId);

    List<AttachmentResponse> findImagesByPostId(Long postId);
    
}