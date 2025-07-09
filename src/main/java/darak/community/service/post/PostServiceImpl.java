package darak.community.service.post;

import darak.community.domain.board.Board;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.domain.post.Post;
import darak.community.infra.repository.BoardRepository;
import darak.community.infra.repository.MemberRepository;
import darak.community.infra.repository.PostRepository;
import darak.community.service.post.request.PostCreateServiceRequest;
import darak.community.service.post.request.PostUpdateServiceRequest;
import darak.community.service.post.response.PostResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void createPost(PostCreateServiceRequest request) {
        Member member = findMemberBy(request.getAuthorId());
        Board board = findBoardBy(request.getBoardId());

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .anonymous(request.getAnonymous())
                .postType(request.getPostType())
                .member(member)
                .board(board)
                .build();

        postRepository.save(post);
    }

    @Override
    @Transactional
    public PostResponse readPostBy(Long postId, Long memberId) {
        Post post = findPostBy(postId);
        Member member = findMemberBy(memberId);
        log.info("{}님이 게시글 {}를 조회합니다.", member.getName(), post.getId());

        increaseReadCount(postId);
        return PostResponse.of(post);
    }


    @Override
    @Transactional
    public void editPost(PostUpdateServiceRequest request) {
        Post post = findPostBy(request.getPostId());
        Member member = findMemberBy(request.getAuthorId());

        if (!hasAuthorization(member, post)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        post.edit(request.getTitle(), request.getContent(), request.getPostType(), request.getAnonymous());
    }

    @Override
    @Transactional
    public void deleteByPostId(Long postId, Long memberId) {
        Post post = findPostBy(postId);
        Member member = findMemberBy(memberId);

        if (!hasAuthorization(member, post)) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void increaseReadCount(Long id) {
        postRepository.findById(id).ifPresent(Post::increaseReadCount);
    }

    @Override
    public Page<PostResponse> findPostsPagedIn(Long boardId, Pageable pageable) {
        List<PostResponse> result = postRepository.findByBoardIdPaged(boardId, pageable)
                .stream()
                .map(PostResponse::of)
                .toList();

        return new PageImpl<>(result, pageable, result.size());
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private Board findBoardBy(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    private boolean hasAuthorization(Member member, Post post) {
        return post.getMember().equals(member) || member.getMemberGrade() == MemberGrade.ADMIN
                || member.getMemberGrade() == MemberGrade.MASTER;
    }
}
