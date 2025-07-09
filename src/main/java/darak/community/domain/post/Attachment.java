package darak.community.domain.post;

import darak.community.domain.BaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Attachment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private UploadFile uploadFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    private Attachment(String url, Long size, String fileType, Post post, String fileName) {
        this.uploadFile = UploadFile.builder()
                .url(url)
                .size(size)
                .fileType(fileType)
                .fileName(fileName)
                .build();
        this.post = post;
    }

    public boolean isImage() {
        return uploadFile.isImage();
    }
}
