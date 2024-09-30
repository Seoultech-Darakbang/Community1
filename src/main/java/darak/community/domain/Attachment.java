package darak.community.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Attachment {
    @Id @GeneratedValue
    private Long id;

    private String url;

    private Long size;

    private TimeStamp timeStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
