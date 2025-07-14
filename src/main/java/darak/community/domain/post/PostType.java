package darak.community.domain.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostType {
    NORMAL("NORMAL", "일반 게시글"),
    FAQ("FAQ", "질문 게시글"),
    NOTICE("NOTICE", "공지사항");

    private final String key;
    private final String value;

}
