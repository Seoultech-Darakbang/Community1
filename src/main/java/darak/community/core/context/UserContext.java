package darak.community.core.context;

public class UserContext {

    private static final ThreadLocal<Long> userHolder = new ThreadLocal<>();

    public static void setCurrentUserId(Long memberId) {
        userHolder.set(memberId);
    }

    public static Long getCurrentMemberId() {
        return userHolder.get();
    }

    public static void clear() {
        userHolder.remove();
    }

} 