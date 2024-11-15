package darak.community.service;

import darak.community.domain.Post;
import java.util.List;

public interface PostService {
    void save(Post post);
    Post findById(Long id);
    void delete(Post post);
    List<Post> findByTitle(String title);
    List<Post> findByMemberName(String memberName);
}
