package darak.community.service.post.response;

import darak.community.domain.post.Attachment;
import darak.community.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GalleryImageResponse {
    
    private String url;
    private PostInfo post;
    
    @Getter
    @Builder
    public static class PostInfo {
        private Long id;
        private String title;
        private Long boardId;
        private String boardName;
        
        public static PostInfo from(Post post) {
            return PostInfo.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .boardId(post.getBoard().getId())
                    .boardName(post.getBoard().getName())
                    .build();
        }
    }
    
    @Builder
    private GalleryImageResponse(String url, PostInfo post) {
        this.url = url;
        this.post = post;
    }
    
    public static GalleryImageResponse fromAttachment(Attachment attachment) {
        return GalleryImageResponse.builder()
                .url(attachment.getUploadFile().getUrl())
                .post(PostInfo.from(attachment.getPost()))
                .build();
    }
    
    public static GalleryImageResponse fromContentImage(String imageUrl, Post post) {
        return GalleryImageResponse.builder()
                .url(imageUrl)
                .post(PostInfo.from(post))
                .build();
    }
} 