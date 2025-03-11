package darak.community.service;

import darak.community.domain.Post;
import darak.community.repository.CommentRepository;
import darak.community.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        commentRepository.findByPostId(id).forEach(commentRepository::delete);
        postRepository.deleteById(id);
    }

    @Override
    public List<Post> findByTitle(String title) {
        return postRepository.findByTitle(title);
    }

    @Override
    public List<Post> findByMemberName(String memberName) {
        return postRepository.findByMemberName(memberName);
    }

    @Override
    @Transactional
    public void increaseReadCount(Long id) {
        postRepository.findById(id).ifPresent(Post::increaseReadCount);
    }

}
