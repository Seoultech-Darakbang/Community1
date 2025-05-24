package darak.community.service;

import darak.community.domain.Attachment;
import darak.community.repository.AttachmentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Override
    @Transactional
    public void save(Attachment attachment) {
        attachmentRepository.save(attachment);
    }

    @Override
    @Transactional
    public void delete(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 첨부파일입니다."));
        attachmentRepository.delete(attachment);
    }

    @Override
    public List<Attachment> findByPostId(Long postId) {
        return attachmentRepository.findByPostId(postId);
    }

    @Override
    public List<Attachment> findImagesByPostId(Long postId) {
        return attachmentRepository.findByPostId(postId).stream()
                .filter(attachment -> {
                    if (attachment.getFileType() != null) {
                        return attachment.getFileType().startsWith("image/");
                    }
                    // fileType이 없는 경우 URL 확장자로 판단
                    String url = attachment.getUrl();
                    return url != null && (url.toLowerCase().endsWith(".jpg") || 
                                         url.toLowerCase().endsWith(".jpeg") || 
                                         url.toLowerCase().endsWith(".png") || 
                                         url.toLowerCase().endsWith(".gif") ||
                                         url.toLowerCase().endsWith(".webp"));
                })
                .collect(Collectors.toList());
    }
}
