package darak.community.service;

import darak.community.domain.Post;
import java.util.List;

public interface PostService {
    void save(Post post);

    Post findById(Long id);

    void deleteById(Long id);

    List<Post> findByTitle(String title);

    List<Post> findByMemberName(String memberName);

    void increaseReadCount(Long id);
}
