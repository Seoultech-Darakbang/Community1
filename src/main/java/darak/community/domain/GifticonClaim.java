package darak.community.domain;

import darak.community.domain.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class GifticonClaim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gifticon_claim_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gifticon_id", nullable = false)
    private Gifticon gifticon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String gifticonCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Builder
    public GifticonClaim(Gifticon gifticon, Member member, String gifticonCode) {
        this.gifticon = gifticon;
        this.member = member;
        this.gifticonCode = gifticonCode;
        this.status = ClaimStatus.CLAIMED;
    }

    public void use() {
        if (this.status != ClaimStatus.CLAIMED) {
            throw new IllegalStateException("이미 사용된 기프티콘입니다.");
        }
        this.status = ClaimStatus.USED;
    }
} 