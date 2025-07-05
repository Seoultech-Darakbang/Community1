package darak.community.domain.member;

public enum MemberGrade {
    NON_MEMBER(0), MEMBER(1), ADMIN(2), MASTER(3);

    private final int precedence;

    MemberGrade(int precedence) {
        this.precedence = precedence;
    }

    public boolean isAtLeast(MemberGrade other) {
        return this.precedence >= other.precedence;
    }
}
