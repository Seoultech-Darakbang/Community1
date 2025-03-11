package darak.community.domain;

import darak.community.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Getter;


@Entity
@Getter
public class Post extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String content;

    private AtomicLong readCount;

    private Boolean anonymous;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public void increaseReadCount() {
        readCount.incrementAndGet();
    }
}
