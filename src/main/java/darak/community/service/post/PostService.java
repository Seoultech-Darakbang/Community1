package darak.community.service.post;

import darak.community.service.post.request.PostCreateServiceRequest;
import darak.community.service.post.request.PostDeleteServiceRequest;
import darak.community.service.post.request.PostUpdateServiceRequest;
import darak.community.service.post.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    void createPost(PostCreateServiceRequest request);

    PostResponse readPostBy(Long postId, Long memberId);

    Page<PostResponse> findPostsPagedIn(Long boardId, Pageable pageable);

    Page<PostResponse> findHeartedPostsBy(Long memberId, Pageable pageable);

    void increaseReadCount(Long id);

    void editPost(PostUpdateServiceRequest request);

    void deletePostBy(Long postId, Long memberId);

    void deletePostByAdmin(PostDeleteServiceRequest request);

    long getTotalPostCount();

}
