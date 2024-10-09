package darak.community.domain;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class Post extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
