package darak.community.core.exception;

public class PasswordExpiredException extends RuntimeException {
    public PasswordExpiredException() {
    }

    public PasswordExpiredException(String message) {
        super(message);
    }
}
