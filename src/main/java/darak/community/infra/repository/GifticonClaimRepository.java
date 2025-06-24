package darak.community.infra.repository;

import darak.community.domain.gifticon.GifticonClaim;
import darak.community.domain.member.Member;
import darak.community.domain.gifticon.Gifticon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GifticonClaimRepository extends JpaRepository<GifticonClaim, Long> {

    boolean existsByGifticonAndMember(Gifticon gifticon, Member member);

    List<GifticonClaim> findByMemberOrderByCreatedDateDesc(Member member);

    Optional<GifticonClaim> findByGifticonCodeAndMember(String gifticonCode, Member member);

    @Query("SELECT COUNT(gc) FROM GifticonClaim gc WHERE gc.gifticon = :gifticon")
    int countByGifticon(@Param("gifticon") Gifticon gifticon);
} 