package darak.community.service;

import darak.community.domain.Attachment;
import darak.community.repository.AttachmentRepository;
import java.util.List;
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
}
