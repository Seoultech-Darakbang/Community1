package darak.community.service.post.response;

import darak.community.domain.post.Attachment;
import darak.community.domain.post.UploadFile;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AttachmentResponse {

    private Long id;
    private String url;
    private Long size;
    private String fileType;
    private String fileName;

    @Builder
    private AttachmentResponse(Long id, String url, Long size, String fileType, String fileName) {
        this.id = id;
        this.url = url;
        this.size = size;
        this.fileType = fileType;
        this.fileName = fileName;
    }

    public static AttachmentResponse from(Attachment attachment) {
        UploadFile file = attachment.getUploadFile();
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .url(file.getUrl())
                .size(file.getSize())
                .fileType(file.getFileType())
                .fileName(file.getFileName())
                .build();
    }
}