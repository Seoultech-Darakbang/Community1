package darak.community.service.post.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSearch {

    private String keyword;
    private String boardName;
    private int page;
    private int size;

    @Builder
    private PostSearch(String keyword, String boardName, int page, int size) {
        this.keyword = keyword;
        this.boardName = boardName;
        this.page = page;
        this.size = size;
    }
}
