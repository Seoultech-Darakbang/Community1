package darak.community.service;

import darak.community.domain.post.Attachment;
import java.util.List;

public interface AttachmentService {
    void save(Attachment attachment);

    void delete(Long attachmentId);

    List<Attachment> findByPostId(Long postId);

    List<Attachment> findImagesByPostId(Long postId);
}
