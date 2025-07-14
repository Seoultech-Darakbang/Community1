package darak.community.domain.post;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class UploadFile {

    private String url;

    private Long size;

    private String fileType;

    private String fileName;

    @Builder
    private UploadFile(String url, Long size, String fileType, String fileName) {
        this.url = url;
        this.size = size;
        this.fileType = fileType;
        this.fileName = fileName;
    }

    public boolean isImage() {
        return fileType != null && fileType.startsWith("image/");
    }
}
