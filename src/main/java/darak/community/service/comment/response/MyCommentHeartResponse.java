package darak.community.service.comment.response;

import darak.community.domain.comment.Comment;
import darak.community.domain.heart.CommentHeart;
import darak.community.domain.member.Member;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCommentHeartResponse {

    private boolean isLiked;
    private int likeCount;

    public static MyCommentHeartResponse of(Member member, Comment comment) {
        List<CommentHeart> commentHearts = comment.getHearts();
        boolean isLiked = commentHearts.stream()
                .anyMatch(commentHeart -> commentHeart.getMember().equals(member));
        int likeCount = commentHearts.size();
        return new MyCommentHeartResponse(isLiked, likeCount);
    }

} 