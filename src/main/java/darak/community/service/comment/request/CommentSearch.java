package darak.community.service.comment.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentSearch {

    private String keyword;
    private String boardName;
    private int page;
    private int size;

    @Builder
    private CommentSearch(String keyword, String boardName, int page, int size) {
        this.keyword = keyword;
        this.boardName = boardName;
        this.page = page;
        this.size = size;
    }
}
