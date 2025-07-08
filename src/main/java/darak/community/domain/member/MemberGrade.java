package darak.community.domain.member;

public enum MemberGrade {
    GUEST(0), MEMBER(1), ADMIN(2), MASTER(3);

    private final int precedence;

    MemberGrade(int precedence) {
        this.precedence = precedence;
    }

    public boolean isAtLeastThan(MemberGrade other) {
        return this.precedence >= other.precedence;
    }
}
