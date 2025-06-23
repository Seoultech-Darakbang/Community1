package darak.community.domain.post;

import darak.community.domain.BaseEntity;
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

    private String url;

    private Long size;

    private String fileType;  // MIME 타입 (예: image/jpeg, image/png)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Attachment(String url, Long size, String fileType, Post post) {
        this.url = url;
        this.size = size;
        this.fileType = fileType;
        this.post = post;
    }

    public boolean isImage() {
        return fileType != null && fileType.startsWith("image/");
    }
}
