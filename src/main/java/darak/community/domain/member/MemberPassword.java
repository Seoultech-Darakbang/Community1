package darak.community.domain.member;

import darak.community.exception.PasswordFailedExceededException;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class MemberPassword {
    private final static int MAX_FAILED_COUNT = 5;
    private final static long TTL = 1209_604; // 2 weeks
    private String password;

    private int failedCount;

    private LocalDateTime expirationDate;

    private long ttl;

    @Builder
    public MemberPassword(String password) {
        this.password = password;
        this.expirationDate = LocalDateTime.now().plusSeconds(TTL);
        this.ttl = TTL; // 2 weeks
        this.failedCount = 0;
    }

    public boolean isMatched(final String rawPassword) throws PasswordFailedExceededException {
        checkFailedCount();
        final boolean matches = isMatches(rawPassword);
        updateFailedCount(matches);
        return matches;
    }

    public boolean isPasswordExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }

    private void checkFailedCount() throws PasswordFailedExceededException {
        if (failedCount >= MAX_FAILED_COUNT) {
            throw new PasswordFailedExceededException();
        }
    }

    private boolean isMatches(String rawPassword) {
        return password.equals(encodePassword(rawPassword));
    }

    private void updateFailedCount(boolean matches) {
        if (matches) {
            failedCount = 0;
            return;
        }

        failedCount++;
    }

    public void changePassword(final String newPassword, final String oldPassword)
            throws PasswordFailedExceededException {
        if (isMatched(oldPassword)) {
            password = encodePassword(newPassword);
            extendExpirationDate();
        }
    }

    private void extendExpirationDate() {
        expirationDate = LocalDateTime.now().plusSeconds(ttl);
    }

    private String encodePassword(String newPassword) {
        return newPassword;
    }
}
