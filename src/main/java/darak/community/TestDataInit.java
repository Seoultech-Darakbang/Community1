package darak.community;

import darak.community.domain.Board;
import darak.community.domain.BoardCategory;
import darak.community.domain.Post;
import darak.community.domain.PostType;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.service.BoardCategoryService;
import darak.community.service.BoardFavoriteService;
import darak.community.service.BoardService;
import darak.community.service.MemberService;
import darak.community.service.PostService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataInit implements ApplicationRunner {

    private final MemberService memberService;
    private final BoardCategoryService boardCategoryService;
    private final BoardService boardService;
    private final PostService postService;
    private final BoardFavoriteService boardFavoriteService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("테스트 데이터 초기화를 시작합니다...");

        // 멤버 데이터 추가
        addMemberData();

        // 게시판 카테고리 데이터 추가
        addBoardCategoryData();

        // 게시판 데이터 추가
        addBoardData();

        // 게시글 데이터 추가
        addPostData();

        // 즐겨찾기 게시판 데이터 추가
        addBoardFavoriteData();

        log.info("테스트 데이터 초기화가 완료되었습니다.");
    }

    private void addMemberData() {
        log.info("회원 테스트 데이터를 추가합니다...");

        // 관리자 회원
        Member adminMember = Member.builder()
                .loginId("admin")
                .name("관리자")
                .password("admin123!")
                .phone("01000000000")
                .birth(LocalDate.of(1990, 1, 1))
                .email("admin@darak.com")
                .grade(MemberGrade.ADMIN)
                .build();
        memberService.join(adminMember);

        // 일반 테스트 회원들
        Member testMember1 = Member.builder()
                .loginId("testuser1")
                .name("김다락")
                .password("test123!")
                .phone("01012345678")
                .birth(LocalDate.of(1995, 5, 15))
                .email("testuser1@test.com")
                .build();
        memberService.join(testMember1);

        Member testMember2 = Member.builder()
                .loginId("testuser2")
                .name("이커뮤")
                .password("test123!")
                .phone("01087654321")
                .birth(LocalDate.of(1992, 8, 20))
                .email("testuser2@test.com")
                .build();
        memberService.join(testMember2);

        Member testMember3 = Member.builder()
                .loginId("testuser3")
                .name("박니티")
                .password("test123!")
                .phone("01055555555")
                .birth(LocalDate.of(1998, 12, 3))
                .email("testuser3@test.com")
                .build();
        memberService.join(testMember3);

        log.info("회원 테스트 데이터 추가 완료 (총 4명)");
    }

    private void addBoardCategoryData() {
        log.info("게시판 카테고리 테스트 데이터를 추가합니다...");

        // 자유게시판 카테고리
        BoardCategory freeCategory = BoardCategory.builder()
                .name("자유게시판")
                .priority(1)
                .build();
        boardCategoryService.save(freeCategory);

        // 질문게시판 카테고리
        BoardCategory questionCategory = BoardCategory.builder()
                .name("질문게시판")
                .priority(2)
                .build();
        boardCategoryService.save(questionCategory);

        // 갤러리 카테고리
        BoardCategory galleryCategory = BoardCategory.builder()
                .name("갤러리")
                .priority(3)
                .build();
        boardCategoryService.save(galleryCategory);

        // 공지사항 카테고리
        BoardCategory noticeCategory = BoardCategory.builder()
                .name("공지사항")
                .priority(4)
                .build();
        boardCategoryService.save(noticeCategory);

        log.info("게시판 카테고리 테스트 데이터 추가 완료 (총 4개)");
    }

    private void addBoardData() {
        log.info("게시판 테스트 데이터를 추가합니다...");

        // 카테고리 조회
        BoardCategory freeCategory = boardCategoryService.findByName("자유게시판");
        BoardCategory questionCategory = boardCategoryService.findByName("질문게시판");
        BoardCategory galleryCategory = boardCategoryService.findByName("갤러리");
        BoardCategory noticeCategory = boardCategoryService.findByName("공지사항");

        // 자유게시판들
        Board freeBoard1 = Board.builder()
                .name("일반 자유게시판")
                .description("자유롭게 이야기를 나누는 공간입니다.")
                .boardCategory(freeCategory)
                .build();
        boardService.save(freeBoard1);

        Board freeBoard2 = Board.builder()
                .name("취미 이야기")
                .description("취미에 관한 이야기를 나누는 공간입니다.")
                .boardCategory(freeCategory)
                .build();
        boardService.save(freeBoard2);

        // 질문게시판들
        Board questionBoard1 = Board.builder()
                .name("개발 질문")
                .description("개발 관련 질문을 하는 공간입니다.")
                .boardCategory(questionCategory)
                .build();
        boardService.save(questionBoard1);

        Board questionBoard2 = Board.builder()
                .name("일반 질문")
                .description("일반적인 질문을 하는 공간입니다.")
                .boardCategory(questionCategory)
                .build();
        boardService.save(questionBoard2);

        // 갤러리 게시판들
        Board galleryBoard1 = Board.builder()
                .name("사진 갤러리")
                .description("아름다운 사진들을 공유하는 공간입니다.")
                .boardCategory(galleryCategory)
                .build();
        boardService.save(galleryBoard1);

        Board galleryBoard2 = Board.builder()
                .name("작품 갤러리")
                .description("창작물과 작품들을 공유하는 공간입니다.")
                .boardCategory(galleryCategory)
                .build();
        boardService.save(galleryBoard2);

        // 공지사항 게시판
        Board noticeBoard = Board.builder()
                .name("전체 공지사항")
                .description("중요한 공지사항을 확인하는 공간입니다.")
                .boardCategory(noticeCategory)
                .build();
        boardService.save(noticeBoard);

        log.info("게시판 테스트 데이터 추가 완료 (총 7개)");
    }

    private void addPostData() {
        log.info("게시글 테스트 데이터를 추가합니다...");

        // 회원 정보 조회
        Member admin = memberService.findByLoginId("admin");
        Member testUser1 = memberService.findByLoginId("testuser1");
        Member testUser2 = memberService.findByLoginId("testuser2");

        // 게시판 정보 조회
        Board noticeBoard = boardService.findByName("전체 공지사항");
        Board freeBoard = boardService.findByName("일반 자유게시판");
        Board questionBoard = boardService.findByName("개발 질문");
        Board galleryBoard = boardService.findByName("사진 갤러리");

        // 공지사항 게시글들
        Post notice1 = Post.builder()
                .title("다락커뮤니티에 오신 것을 환영합니다!")
                .content("안녕하세요! 다락커뮤니티를 이용해주셔서 감사합니다.\n\n이곳은 자유롭게 소통할 수 있는 공간입니다.\n서로 존중하며 즐거운 커뮤니티를 만들어가요!")
                .postType(PostType.NOTICE)
                .member(admin)
                .board(noticeBoard)
                .anonymous(false)
                .build();
        postService.save(notice1);

        Post notice2 = Post.builder()
                .title("커뮤니티 이용 규칙 안내")
                .content("커뮤니티 이용 시 다음 규칙을 준수해주세요:\n\n1. 상호 존중하기\n2. 스팸성 게시물 금지\n3. 개인정보 보호하기\n4. 건전한 토론 문화 만들기")
                .postType(PostType.NOTICE)
                .member(admin)
                .board(noticeBoard)
                .anonymous(false)
                .build();
        postService.save(notice2);

        // 자유게시판 게시글들
        Post freePost1 = Post.builder()
                .title("안녕하세요! 처음 가입했습니다")
                .content("다락커뮤니티에 처음 가입한 김다락이라고 합니다.\n앞으로 잘 부탁드려요! 😊\n\n취미는 독서와 영화감상이에요.\n비슷한 취미 가진 분들과 이야기 나누고 싶어요!")
                .postType(PostType.NORMAL)
                .member(testUser1)
                .board(freeBoard)
                .anonymous(false)
                .build();
        postService.save(freePost1);

        Post freePost2 = Post.builder()
                .title("오늘 날씨가 정말 좋네요")
                .content("창밖을 보니 햇살이 너무 좋아서 기분이 좋아지네요.\n이런 날엔 산책하기 딱 좋을 것 같아요.\n\n여러분은 좋은 날씨에 뭘 하시나요?")
                .postType(PostType.NORMAL)
                .member(testUser2)
                .board(freeBoard)
                .anonymous(false)
                .build();
        postService.save(freePost2);

        // 질문게시판 게시글들
        Post questionPost1 = Post.builder()
                .title("스프링부트 관련 질문 있습니다")
                .content("스프링부트로 웹 애플리케이션을 개발하고 있는데,\nJPA 연관관계 매핑에서 자꾸 에러가 발생해요.\n\n혹시 비슷한 경험 있으신 분 계시나요?\n조언 부탁드립니다!")
                .postType(PostType.FAQ)
                .member(testUser1)
                .board(questionBoard)
                .anonymous(false)
                .build();
        postService.save(questionPost1);

        Post questionPost2 = Post.builder()
                .title("추천 도서가 있을까요?")
                .content("요즘 개발 공부를 시작했는데,\n입문자에게 좋은 프로그래밍 책을 추천해주실 수 있나요?\n\nJava나 웹 개발 관련 책이면 더 좋겠어요.")
                .postType(PostType.FAQ)
                .member(testUser2)
                .board(questionBoard)
                .anonymous(false)
                .build();
        postService.save(questionPost2);

        // 갤러리 게시글들
        Post galleryPost1 = Post.builder()
                .title("오늘 찍은 일몰 사진입니다")
                .content("한강에서 찍은 일몰 사진이에요.\n정말 예쁘게 나온 것 같아서 공유합니다! 📸\n\n날씨가 좋아서 사진이 잘 나왔네요.")
                .postType(PostType.NORMAL)
                .member(testUser1)
                .board(galleryBoard)
                .anonymous(false)
                .build();
        postService.save(galleryPost1);

        Post galleryPost2 = Post.builder()
                .title("제가 그린 디지털 아트 작품입니다")
                .content("요즘 디지털 드로잉에 빠져있어요.\n처음 완성한 작품이라서 부족하지만 공유해봅니다.\n\n피드백 환영합니다! 🎨")
                .postType(PostType.NORMAL)
                .member(testUser2)
                .board(galleryBoard)
                .anonymous(false)
                .build();
        postService.save(galleryPost2);

        log.info("게시글 테스트 데이터 추가 완료 (총 8개)");
    }

    private void addBoardFavoriteData() {
        log.info("즐겨찾기 게시판 테스트 데이터를 추가합니다...");

        // 회원 정보 조회
        Member testUser1 = memberService.findByLoginId("testuser1");
        Member testUser2 = memberService.findByLoginId("testuser2");
        Member testUser3 = memberService.findByLoginId("testuser3");

        // 게시판 정보 조회
        Board freeBoard = boardService.findByName("일반 자유게시판");
        Board questionBoard = boardService.findByName("개발 질문");
        Board galleryBoard = boardService.findByName("사진 갤러리");
        Board hobbyBoard = boardService.findByName("취미 이야기");

        // testUser1의 즐겨찾기
        boardFavoriteService.addFavorite(testUser1, freeBoard);
        boardFavoriteService.addFavorite(testUser1, questionBoard);
        boardFavoriteService.addFavorite(testUser1, galleryBoard);

        // testUser2의 즐겨찾기
        boardFavoriteService.addFavorite(testUser2, freeBoard);
        boardFavoriteService.addFavorite(testUser2, hobbyBoard);

        // testUser3의 즐겨찾기
        boardFavoriteService.addFavorite(testUser3, galleryBoard);
        boardFavoriteService.addFavorite(testUser3, questionBoard);

        log.info("즐겨찾기 게시판 테스트 데이터 추가 완료");
    }
} 