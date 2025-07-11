package darak.community.service.post;

import darak.community.core.auth.ServiceAuth;
import darak.community.domain.board.Board;
import darak.community.domain.heart.PostHeart;
import darak.community.domain.log.AdminLog;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.domain.post.Post;
import darak.community.infra.repository.AdminLogRepository;
import darak.community.infra.repository.BoardRepository;
import darak.community.infra.repository.MemberRepository;
import darak.community.infra.repository.PostHeartRepository;
import darak.community.infra.repository.PostRepository;
import darak.community.infra.repository.dto.PostWithMetaDto;
import darak.community.service.post.request.PostCreateServiceRequest;
import darak.community.service.post.request.PostDeleteServiceRequest;
import darak.community.service.post.request.PostSearch;
import darak.community.service.post.request.PostUpdateServiceRequest;
import darak.community.service.post.response.PostResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private final AdminLogRepository adminLogRepository;
    private final PostHeartRepository postHeartRepository;

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
    public Page<PostWithMetaDto> findPostsWithMetaByMemberPaged(Long memberId, Pageable pageable) {
        return postRepository.findPostsWithMetaByMemberId(memberId, pageable);
    }

    @Override
    @Transactional
    public void editPost(PostUpdateServiceRequest request) {
        Post post = findPostBy(request.getPostId());
        Member member = findMemberBy(request.getAuthorId());

        validateAuthor(member, post);

        post.edit(request.getTitle(), request.getContent(), request.getPostType(), request.getAnonymous());
    }

    @Override
    @Transactional
    public void deletePostBy(Long postId, Long memberId) {
        Post post = findPostBy(postId);
        Member member = findMemberBy(memberId);

        validateAuthor(member, post);

        // TODO: 단방향 POST - HEART 매핑 vs 양방향 매핑 비교정리
        List<PostHeart> hearts = postHeartRepository.findByPostId(postId);
        hearts.forEach(postHeartRepository::delete);
    }

    @Override
    @Transactional
    @ServiceAuth(MemberGrade.ADMIN)
    public void deletePostByAdmin(PostDeleteServiceRequest request) {
        Post post = findPostBy(request.getPostId());
        Member admin = findMemberBy(request.getMemberId());

        adminLogRepository.save(AdminLog.postDeleteLog(post, admin, request.getReason()));
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

    @Override
    public Page<PostResponse> findHeartedPostsBy(Long memberId, Pageable pageable) {
        Page<PostHeart> postHearts = postHeartRepository.findByMemberIdFetchPost(memberId, pageable);
        return postHearts.map(postHeart -> PostResponse.of(postHeart.getPost()));
    }

    @Override
    public long getTotalPostCount() {
        return postRepository.count();
    }

    @Override
    public Page<PostWithMetaDto> searchPostsByMemberIdAnd(Long memberId, PostSearch postSearch) {
        Member member = findMemberBy(memberId);
        Pageable pageable = PageRequest.of(postSearch.getPage(), postSearch.getSize());
        Page<PostWithMetaDto> posts = postRepository.findPostsWithMetaByMemberId(memberId, pageable);
        // 여기서 검색 수행
        if (postSearch.getKeyword() == null || postSearch.getKeyword().isEmpty()) {
            return posts;
        }
        List<PostWithMetaDto> result = posts.stream()
                .filter(post -> post.getTitle().contains(postSearch.getKeyword())
                        || post.getBoardName().contains(postSearch.getBoardName()))
                .toList();

        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public Page<PostWithMetaDto> findPostsByMemberIdAndLiked(Long memberId, Pageable pageable) {
        return postRepository.findPostsWithMetaByMemberLiked(memberId, pageable);
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

    private void validateAuthor(Member member, Post post) {
        if (!isMemberAuthor(member, post)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }

    private boolean isMemberAuthor(Member member, Post post) {
        return post.getMember().equals(member);
    }
}
