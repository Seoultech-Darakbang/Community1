package darak.community.repository;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonStatus;
import darak.community.infra.repository.GifticonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("GifticonRepository 테스트")
class GifticonRepositoryTest {

    @Autowired
    private GifticonRepository gifticonRepository;

    @Test
    @DisplayName("기프티콘 저장 및 조회")
    void saveAndFindGifticon() {
        // given
        Gifticon gifticon = createTestGifticon();

        // when
        Gifticon saved = gifticonRepository.save(gifticon);
        Optional<Gifticon> found = gifticonRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("테스트 기프티콘");
        assertThat(found.get().getBrand()).isEqualTo("테스트 브랜드");
    }

    @Test
    @DisplayName("활성 기프티콘 조회")
    void findActiveGifticons() {
        // given
        Gifticon activeGifticon = createTestGifticon();
        activeGifticon.activate();
        gifticonRepository.save(activeGifticon);

        Gifticon waitingGifticon = createTestGifticon();
        gifticonRepository.save(waitingGifticon);

        // when
        List<Gifticon> activeGifticons = gifticonRepository.findActiveGifticons(
                GifticonStatus.ACTIVE, LocalDateTime.now().plusDays(10));

        // then
        assertThat(activeGifticons).hasSize(1);
        assertThat(activeGifticons.get(0).getStatus()).isEqualTo(GifticonStatus.ACTIVE);
    }

    @Test
    @DisplayName("기프티콘 삭제")
    void deleteGifticon() {
        // given
        Gifticon gifticon = createTestGifticon();
        Gifticon saved = gifticonRepository.save(gifticon);

        // when
        gifticonRepository.deleteById(saved.getId());
        Optional<Gifticon> found = gifticonRepository.findById(saved.getId());

        // then
        assertThat(found).isEmpty();
    }

    private Gifticon createTestGifticon() {
        return Gifticon.builder()
                .title("테스트 기프티콘")
                .description("테스트 설명")
                .brand("테스트 브랜드")
                .totalQuantity(100)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
    }
} 