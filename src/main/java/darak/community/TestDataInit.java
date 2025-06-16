package darak.community;

import darak.community.domain.Attachment;
import darak.community.domain.Board;
import darak.community.domain.BoardCategory;
import darak.community.domain.Comment;
import darak.community.domain.Post;
import darak.community.domain.PostType;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.service.AttachmentService;
import darak.community.service.BoardCategoryService;
import darak.community.service.BoardFavoriteService;
import darak.community.service.BoardService;
import darak.community.service.CommentHeartService;
import darak.community.service.CommentService;
import darak.community.service.MemberService;
import darak.community.service.PostHeartService;
import darak.community.service.PostService;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local")
public class TestDataInit implements ApplicationRunner {

    private final MemberService memberService;
    private final BoardCategoryService boardCategoryService;
    private final BoardService boardService;
    private final PostService postService;
    private final CommentService commentService;
    private final BoardFavoriteService boardFavoriteService;
    private final AttachmentService attachmentService;
    private final PostHeartService postHeartService;
    private final CommentHeartService commentHeartService;

    private final Random random = new Random();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("다락방 동아리 테스트 데이터 초기화를 시작합니다...");

        // 회원 데이터 추가
        addMemberData();

        // 게시판 카테고리 데이터 추가
        addBoardCategoryData();

        // 게시판 데이터 추가
        addBoardData();

        // 게시글 데이터 추가
        addPostData();

        // 댓글 데이터 추가
        addCommentData();

        // 좋아요 데이터 추가
        addHeartData();

        // 즐겨찾기 게시판 데이터 추가
        addBoardFavoriteData();

        log.info("다락방 동아리 테스트 데이터 초기화가 완료되었습니다.");
    }

    private void addMemberData() {
        log.info("다락방 동아리 회원 테스트 데이터를 추가합니다...");

        // 관리자 (동아리 회장)
        Member president = Member.builder()
                .loginId("president2024")
                .name("김동아리")
                .password("darak123!")
                .phone("01012345678")
                .birth(LocalDate.of(2001, 3, 15))
                .email("president@seoultech.ac.kr")
                .grade(MemberGrade.ADMIN)
                .build();
        memberService.join(president);

        // 부회장
        Member vicePresident = Member.builder()
                .loginId("vice2024")
                .name("이부회장")
                .password("darak123!")
                .phone("01087654321")
                .birth(LocalDate.of(2002, 7, 22))
                .email("vice@seoultech.ac.kr")
                .grade(MemberGrade.ADMIN)
                .build();
        memberService.join(vicePresident);

        // 일반 동아리 회원들 (3~4학년)
        String[] seniorNames = {
                "박선배", "최코딩", "정알고리즘", "강데이터베이스", "유네트워크",
                "윤운영체제", "임컴구조", "한프로그래밍", "조소프트웨어", "신웹개발"
        };

        for (int i = 0; i < seniorNames.length; i++) {
            Member senior = Member.builder()
                    .loginId("senior" + (i + 1))
                    .name(seniorNames[i])
                    .password("darak123!")
                    .phone("0109" + String.format("%07d", 1000000 + i))
                    .birth(LocalDate.of(2000 + random.nextInt(2), random.nextInt(12) + 1, random.nextInt(28) + 1))
                    .email("senior" + (i + 1) + "@seoultech.ac.kr")
                    .grade(MemberGrade.MEMBER)
                    .build();
            memberService.join(senior);
        }

        // 일반 동아리 회원들 (1~2학년)
        String[] juniorNames = {
                "김신입", "이프로그래밍", "박코딩", "최개발", "정자바", "강스프링",
                "유리액트", "윤파이썬", "임씨플플", "한자바스크립트", "조데이터베이스", "신알고리즘",
                "백엔드", "프론트엔드", "김풀스택", "이모바일", "박인공지능", "최머신러닝"
        };

        for (int i = 0; i < juniorNames.length; i++) {
            Member junior = Member.builder()
                    .loginId("junior" + (i + 1))
                    .name(juniorNames[i])
                    .password("darak123!")
                    .phone("0108" + String.format("%07d", 2000000 + i))
                    .birth(LocalDate.of(2003 + random.nextInt(2), random.nextInt(12) + 1, random.nextInt(28) + 1))
                    .email("junior" + (i + 1) + "@seoultech.ac.kr")
                    .grade(MemberGrade.MEMBER)
                    .build();
            memberService.join(junior);
        }

        log.info("다락방 동아리 회원 테스트 데이터 추가 완료 (총 30명)");
    }

    private void addBoardCategoryData() {
        log.info("게시판 카테고리 테스트 데이터를 추가합니다...");

        // 공지사항
        BoardCategory noticeCategory = BoardCategory.builder()
                .name("공지사항")
                .priority(1)
                .build();
        boardCategoryService.save(noticeCategory);

        // 자유게시판
        BoardCategory freeCategory = BoardCategory.builder()
                .name("자유게시판")
                .priority(2)
                .build();
        boardCategoryService.save(freeCategory);

        // 스터디
        BoardCategory studyCategory = BoardCategory.builder()
                .name("스터디")
                .priority(3)
                .build();
        boardCategoryService.save(studyCategory);

        // 프로젝트
        BoardCategory projectCategory = BoardCategory.builder()
                .name("프로젝트")
                .priority(4)
                .build();
        boardCategoryService.save(projectCategory);

        // 취업/진로
        BoardCategory careerCategory = BoardCategory.builder()
                .name("취업/진로")
                .priority(5)
                .build();
        boardCategoryService.save(careerCategory);

        // 갤러리
        BoardCategory galleryCategory = BoardCategory.builder()
                .name("갤러리")
                .priority(6)
                .build();
        boardCategoryService.save(galleryCategory);

        // Q&A
        BoardCategory qnaCategory = BoardCategory.builder()
                .name("Q&A")
                .priority(7)
                .build();
        boardCategoryService.save(qnaCategory);

        log.info("게시판 카테고리 테스트 데이터 추가 완료 (총 7개)");
    }

    private void addBoardData() {
        log.info("게시판 테스트 데이터를 추가합니다...");

        // 카테고리 조회
        BoardCategory noticeCategory = boardCategoryService.findByName("공지사항");
        BoardCategory freeCategory = boardCategoryService.findByName("자유게시판");
        BoardCategory studyCategory = boardCategoryService.findByName("스터디");
        BoardCategory projectCategory = boardCategoryService.findByName("프로젝트");
        BoardCategory careerCategory = boardCategoryService.findByName("취업/진로");
        BoardCategory galleryCategory = boardCategoryService.findByName("갤러리");
        BoardCategory qnaCategory = boardCategoryService.findByName("Q&A");

        // 공지사항 게시판
        Board noticeBoard = Board.builder()
                .name("전체 공지사항")
                .description("다락방 동아리 공지사항을 확인하는 게시판입니다.")
                .boardCategory(noticeCategory)
                .priority(1)
                .build();
        boardService.save(noticeBoard);

        Board eventBoard = Board.builder()
                .name("행사 안내")
                .description("동아리 행사 및 활동 안내 게시판입니다.")
                .boardCategory(noticeCategory)
                .priority(2)
                .build();
        boardService.save(eventBoard);

        // 자유게시판
        Board freeBoard = Board.builder()
                .name("자유 게시판")
                .description("자유롭게 이야기를 나누는 공간입니다.")
                .boardCategory(freeCategory)
                .priority(1)
                .build();
        boardService.save(freeBoard);

        Board hobbyBoard = Board.builder()
                .name("취미 이야기")
                .description("개발 외 취미와 관심사를 공유하는 게시판입니다.")
                .boardCategory(freeCategory)
                .priority(2)
                .build();
        boardService.save(hobbyBoard);

        // 스터디 게시판
        Board algorithmStudy = Board.builder()
                .name("알고리즘 스터디")
                .description("코딩테스트 및 알고리즘 문제 해결 스터디 게시판입니다.")
                .boardCategory(studyCategory)
                .priority(1)
                .build();
        boardService.save(algorithmStudy);

        Board webStudy = Board.builder()
                .name("웹 개발 스터디")
                .description("프론트엔드/백엔드 웹 개발 스터디 게시판입니다.")
                .boardCategory(studyCategory)
                .priority(2)
                .build();
        boardService.save(webStudy);

        Board csStudy = Board.builder()
                .name("CS 기초 스터디")
                .description("컴퓨터과학 기초 지식 스터디 게시판입니다.")
                .boardCategory(studyCategory)
                .priority(3)
                .build();
        boardService.save(csStudy);

        // 프로젝트 게시판
        Board teamProject = Board.builder()
                .name("팀 프로젝트")
                .description("팀 프로젝트 모집 및 진행 상황을 공유하는 게시판입니다.")
                .boardCategory(projectCategory)
                .priority(1)
                .build();
        boardService.save(teamProject);

        Board portfolioProject = Board.builder()
                .name("포트폴리오 프로젝트")
                .description("개인 포트폴리오 프로젝트를 공유하는 게시판입니다.")
                .boardCategory(projectCategory)
                .priority(2)
                .build();
        boardService.save(portfolioProject);

        // 취업/진로 게시판
        Board jobInfo = Board.builder()
                .name("채용 정보")
                .description("IT 기업 채용 정보를 공유하는 게시판입니다.")
                .boardCategory(careerCategory)
                .priority(1)
                .build();
        boardService.save(jobInfo);

        Board interviewReview = Board.builder()
                .name("면접 후기")
                .description("IT 기업 면접 후기를 공유하는 게시판입니다.")
                .boardCategory(careerCategory)
                .priority(2)
                .build();
        boardService.save(interviewReview);

        // 갤러리 게시판
        Board photoGallery = Board.builder()
                .name("활동 사진 갤러리")
                .description("동아리 활동 사진을 공유하는 갤러리입니다.")
                .boardCategory(galleryCategory)
                .priority(1)
                .build();
        boardService.save(photoGallery);

        Board projectGallery = Board.builder()
                .name("프로젝트 갤러리")
                .description("완성된 프로젝트 결과물을 공유하는 갤러리입니다.")
                .boardCategory(galleryCategory)
                .priority(2)
                .build();
        boardService.save(projectGallery);

        // Q&A 게시판
        Board devQna = Board.builder()
                .name("개발 Q&A")
                .description("개발 관련 질문과 답변을 나누는 게시판입니다.")
                .boardCategory(qnaCategory)
                .priority(1)
                .build();
        boardService.save(devQna);

        Board generalQna = Board.builder()
                .name("일반 Q&A")
                .description("일반적인 질문과 답변을 나누는 게시판입니다.")
                .boardCategory(qnaCategory)
                .priority(2)
                .build();
        boardService.save(generalQna);

        log.info("게시판 테스트 데이터 추가 완료 (총 14개)");
    }

    private void addPostData() {
        log.info("게시글 테스트 데이터를 추가합니다...");

        // 회원과 게시판 조회
        Member president = memberService.findByLoginId("president2024");
        Member vicePresident = memberService.findByLoginId("vice2024");

        // 공지사항 게시글
        addNoticePostData(president, vicePresident);

        // 자유게시판 게시글
        addFreePostData();

        // 스터디 게시글
        addStudyPostData();

        // 프로젝트 게시글
        addProjectPostData();

        // 취업/진로 게시글
        addCareerPostData();

        // 갤러리 게시글
        addGalleryPostData();

        // Q&A 게시글
        addQnaPostData();

        log.info("게시글 테스트 데이터 추가 완료");
    }

    private void addNoticePostData(Member president, Member vicePresident) {
        Board noticeBoard = boardService.findByName("전체 공지사항");
        Board eventBoard = boardService.findByName("행사 안내");

        // 전체 공지사항
        String[] noticeTitles = {
                "다락방 동아리 2024년 활동 계획 안내",
                "정기 모임 시간 변경 안내",
                "동아리 회비 납부 안내",
                "코로나19 방역 수칙 준수 안내",
                "동아리실 이용 규칙 안내"
        };

        String[] noticeContents = {
                "안녕하세요, 다락방 동아리 회장입니다.\n\n2024년 동아리 활동 계획을 안내드립니다.\n\n주요 활동:\n- 정기 스터디 (매주 수요일 18:00)\n- 프로젝트 발표회 (학기말)\n- 기업 견학 (하계 방학)\n- 졸업생 초청 세미나 (동계 방학)\n\n많은 참여 부탁드립니다.",
                "정기 모임 시간이 매주 수요일 18:00에서 19:00으로 변경됩니다.\n참고하시어 참석 부탁드립니다.",
                "2024년 1학기 동아리 회비 납부 안내입니다.\n회비: 5만원\n계좌: 국민은행 123-456-789012\n납부 기한: 3월 31일까지",
                "동아리 활동 시 코로나19 방역 수칙을 준수해주세요.\n- 마스크 착용\n- 손 소독\n- 거리두기",
                "동아리실 이용 시 다음 규칙을 준수해주세요:\n- 사용 후 정리정돈\n- 음식물 반입 금지\n- 퇴실 시 전원 차단"
        };

        for (int i = 0; i < noticeTitles.length; i++) {
            Post notice = Post.builder()
                    .title(noticeTitles[i])
                    .content(noticeContents[i])
                    .postType(PostType.NOTICE)
                    .member(i % 2 == 0 ? president : vicePresident)
                    .board(noticeBoard)
                    .anonymous(false)
                    .build();
            postService.save(notice);
        }

        // 행사 안내
        String[] eventTitles = {
                "2024 신입생 환영회 안내",
                "겨울 프로그래밍 캠프 참가자 모집",
                "IT 기업 견학 신청 안내",
                "해커톤 대회 참가팀 모집"
        };

        for (int i = 0; i < eventTitles.length; i++) {
            Post event = Post.builder()
                    .title(eventTitles[i])
                    .content("행사 관련 자세한 내용은 추후 공지하겠습니다.\n많은 관심과 참여 부탁드립니다.")
                    .postType(PostType.NOTICE)
                    .member(vicePresident)
                    .board(eventBoard)
                    .anonymous(false)
                    .build();
            postService.save(event);
        }
    }

    private void addFreePostData() {
        Board freeBoard = boardService.findByName("자유 게시판");
        Board hobbyBoard = boardService.findByName("취미 이야기");

        // 자유게시판 게시글 (많은 수의 데이터로 페이징 테스트)
        String[] freeTitles = {
                "안녕하세요! 새로 가입한 신입생입니다",
                "오늘 날씨가 정말 좋네요",
                "학교 맛집 추천해주세요",
                "중간고사 화이팅!",
                "동아리 활동 정말 재미있어요",
                "프로그래밍 언어 추천 부탁드려요",
                "방학 계획이 있으신가요?",
                "카페 추천해주세요",
                "요즘 핫한 드라마가 뭔가요?",
                "운동 같이 하실 분 계신가요?",
                "독서 모임 만들어볼까요?",
                "게임 추천 부탁드려요",
                "영화 리뷰 공유해요",
                "요리 레시피 공유",
                "여행 후기 공유합니다",
                "스트레스 해소법 공유해요",
                "좋은 음악 추천 부탁드려요",
                "사진 찍기 좋은 곳 추천",
                "펜션 추천해주세요",
                "맛있는 음식점 후기"
        };

        for (int i = 0; i < freeTitles.length; i++) {
            Member randomMember = getRandomMember();
            Post freePost = Post.builder()
                    .title(freeTitles[i])
                    .content("자유게시판 게시글 내용입니다. " + (i + 1) + "번째 글이에요.\n\n많은 관심과 댓글 부탁드려요!")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(freeBoard)
                    .anonymous(random.nextBoolean())
                    .build();
            postService.save(freePost);
        }

        // 취미 이야기 게시판
        String[] hobbyTitles = {
                "독서 모임 만들어요",
                "보드게임 같이 하실 분?",
                "등산 동호회 모집",
                "영화 감상 모임",
                "요리 배우고 싶어요",
                "사진 촬영 취미",
                "기타 배우기 도전",
                "그림 그리기 시작했어요"
        };

        for (int i = 0; i < hobbyTitles.length; i++) {
            Member randomMember = getRandomMember();
            Post hobbyPost = Post.builder()
                    .title(hobbyTitles[i])
                    .content("취미 관련 게시글입니다. 같은 취미를 가진 분들과 함께 활동하고 싶어요!")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(hobbyBoard)
                    .anonymous(false)
                    .build();
            postService.save(hobbyPost);
        }
    }

    private void addStudyPostData() {
        Board algorithmStudy = boardService.findByName("알고리즘 스터디");
        Board webStudy = boardService.findByName("웹 개발 스터디");
        Board csStudy = boardService.findByName("CS 기초 스터디");

        // 알고리즘 스터디
        String[] algorithmTitles = {
                "백준 단계별 풀이 스터디원 모집",
                "프로그래머스 Level 2 문제 풀이",
                "코딩테스트 대비 스터디",
                "삼성 SW 역량테스트 준비",
                "카카오 코딩테스트 후기",
                "동적계획법 문제 풀이",
                "그래프 알고리즘 스터디",
                "이분탐색 문제 모음"
        };

        for (String title : algorithmTitles) {
            Member randomMember = getRandomMember();
            Post algorithmPost = Post.builder()
                    .title(title)
                    .content("알고리즘 스터디 관련 게시글입니다.\n\n함께 공부하며 실력을 향상시켜요!")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(algorithmStudy)
                    .anonymous(false)
                    .build();
            postService.save(algorithmPost);
        }

        // 웹 개발 스터디
        String[] webTitles = {
                "React 스터디원 모집합니다",
                "Spring Boot 프로젝트 스터디",
                "Node.js 백엔드 개발 스터디",
                "Vue.js 입문 스터디",
                "TypeScript 스터디",
                "Next.js 프레임워크 스터디",
                "REST API 설계 스터디",
                "데이터베이스 설계 스터디"
        };

        for (String title : webTitles) {
            Member randomMember = getRandomMember();
            Post webPost = Post.builder()
                    .title(title)
                    .content("웹 개발 스터디 관련 게시글입니다.\n\n실무에 도움되는 기술을 함께 배워요!")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(webStudy)
                    .anonymous(false)
                    .build();
            postService.save(webPost);
        }

        // CS 기초 스터디
        String[] csTitles = {
                "운영체제 개념 정리 스터디",
                "네트워크 기초 스터디",
                "데이터베이스 이론 스터디",
                "컴퓨터구조 스터디",
                "자료구조 알고리즘 스터디",
                "디자인패턴 스터디"
        };

        for (String title : csTitles) {
            Member randomMember = getRandomMember();
            Post csPost = Post.builder()
                    .title(title)
                    .content("CS 기초 스터디 관련 게시글입니다.\n\n탄탄한 기초 지식을 쌓아요!")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(csStudy)
                    .anonymous(false)
                    .build();
            postService.save(csPost);
        }
    }

    private void addProjectPostData() {
        Board teamProject = boardService.findByName("팀 프로젝트");
        Board portfolioProject = boardService.findByName("포트폴리오 프로젝트");

        // 팀 프로젝트
        String[] teamTitles = {
                "웹 쇼핑몰 프로젝트 팀원 모집",
                "모바일 앱 개발 프로젝트",
                "AI 챗봇 개발 프로젝트",
                "게임 개발 프로젝트 팀원 구합니다",
                "블록체인 프로젝트",
                "IoT 프로젝트 함께 하실 분",
                "빅데이터 분석 프로젝트",
                "클라우드 서비스 프로젝트"
        };

        for (String title : teamTitles) {
            Member randomMember = getRandomMember();
            Post teamPost = Post.builder()
                    .title(title)
                    .content("팀 프로젝트 팀원을 모집합니다.\n\n관심 있으신 분들의 많은 지원 바랍니다!")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(teamProject)
                    .anonymous(false)
                    .build();
            postService.save(teamPost);
        }

        // 포트폴리오 프로젝트
        String[] portfolioTitles = {
                "개인 블로그 사이트 제작기",
                "Todo 앱 개발 후기",
                "날씨 API를 활용한 앱",
                "포트폴리오 웹사이트 제작",
                "간단한 게임 제작기",
                "데이터 시각화 프로젝트"
        };

        for (String title : portfolioTitles) {
            Member randomMember = getRandomMember();
            Post portfolioPost = Post.builder()
                    .title(title)
                    .content("개인 포트폴리오 프로젝트를 공유합니다.\n\n피드백 주시면 감사하겠습니다!")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(portfolioProject)
                    .anonymous(false)
                    .build();
            postService.save(portfolioPost);
        }
    }

    private void addCareerPostData() {
        Board jobInfo = boardService.findByName("채용 정보");
        Board interviewReview = boardService.findByName("면접 후기");

        // 채용 정보
        String[] jobTitles = {
                "네이버 신입 개발자 채용 공고",
                "카카오 인턴십 모집 안내",
                "라인 개발자 채용",
                "쿠팡 백엔드 개발자 모집",
                "NHN 신입 개발자 채용",
                "SK텔레콤 개발자 채용",
                "삼성전자 소프트웨어 직군 채용",
                "LG전자 개발자 채용 정보"
        };

        for (String title : jobTitles) {
            Member randomMember = getRandomMember();
            Post jobPost = Post.builder()
                    .title(title)
                    .content("IT 기업 채용 정보를 공유합니다.\n\n관심 있으신 분들은 지원해보세요!")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(jobInfo)
                    .anonymous(false)
                    .build();
            postService.save(jobPost);
        }

        // 면접 후기
        String[] interviewTitles = {
                "네이버 개발자 면접 후기",
                "카카오 인턴 면접 경험담",
                "라인 면접 준비 팁",
                "스타트업 면접 후기",
                "대기업 면접 경험 공유",
                "코딩테스트 합격 후기"
        };

        for (String title : interviewTitles) {
            Member randomMember = getRandomMember();
            Post interviewPost = Post.builder()
                    .title(title)
                    .content("면접 후기를 공유합니다.\n\n취업 준비하시는 분들께 도움이 되길 바랍니다!")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(interviewReview)
                    .anonymous(true)
                    .build();
            postService.save(interviewPost);
        }
    }

    private void addGalleryPostData() {
        Board photoGallery = boardService.findByName("활동 사진 갤러리");
        Board projectGallery = boardService.findByName("프로젝트 갤러리");

        // 활동 사진 갤러리
        String[] photoTitles = {
                "2024 신입생 환영회 사진",
                "정기 모임 사진",
                "해커톤 대회 참가 사진",
                "기업 견학 사진",
                "졸업생 초청 세미나 사진",
                "동아리 MT 사진",
                "프로젝트 발표회 사진",
                "겨울 프로그래밍 캠프 사진"
        };

        for (String title : photoTitles) {
            Member randomMember = getRandomMember();
            Post photoPost = Post.builder()
                    .title(title)
                    .content("동아리 활동 사진을 공유합니다!\n\n즐거운 추억이 담긴 사진들이에요 📸")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(photoGallery)
                    .anonymous(false)
                    .build();
            postService.save(photoPost);

            // 갤러리 이미지 추가
            addGalleryImages(photoPost);
        }

        // 프로젝트 갤러리
        String[] projectGalleryTitles = {
                "웹 쇼핑몰 프로젝트 결과물",
                "모바일 앱 UI/UX 디자인",
                "AI 프로젝트 시연 영상",
                "게임 개발 스크린샷",
                "데이터 시각화 결과물",
                "웹사이트 디자인 모음"
        };

        for (String title : projectGalleryTitles) {
            Member randomMember = getRandomMember();
            Post projectPost = Post.builder()
                    .title(title)
                    .content("프로젝트 결과물을 공유합니다!\n\n열심히 개발한 결과물이에요 💻")
                    .postType(PostType.NORMAL)
                    .member(randomMember)
                    .board(projectGallery)
                    .anonymous(false)
                    .build();
            postService.save(projectPost);

            // 갤러리 이미지 추가
            addGalleryImages(projectPost);
        }
    }

    private void addQnaPostData() {
        Board devQna = boardService.findByName("개발 Q&A");
        Board generalQna = boardService.findByName("일반 Q&A");

        // 개발 Q&A
        String[] devQnaTitles = {
                "Spring Boot에서 JPA 연관관계 설정 질문",
                "React Hook 사용법 문의",
                "Python 크롤링 에러 해결",
                "Git 브랜치 관리 방법",
                "Docker 컨테이너 배포 문제",
                "MySQL 쿼리 최적화 방법",
                "JavaScript 비동기 처리 질문",
                "CSS 반응형 디자인 문의",
                "Node.js Express 미들웨어 사용법",
                "Vue.js 컴포넌트 통신 방법"
        };

        for (String title : devQnaTitles) {
            Member randomMember = getRandomMember();
            Post devQnaPost = Post.builder()
                    .title(title)
                    .content("개발 관련 질문입니다.\n\n도움 주시면 정말 감사하겠습니다!")
                    .postType(PostType.FAQ)
                    .member(randomMember)
                    .board(devQna)
                    .anonymous(false)
                    .build();
            postService.save(devQnaPost);
        }

        // 일반 Q&A
        String[] generalQnaTitles = {
                "컴퓨터공학과 전공 선택 조언",
                "대학원 진학 vs 취업 고민",
                "프로그래밍 언어 선택 조언",
                "개발자 로드맵 문의",
                "포트폴리오 작성 팁",
                "코딩테스트 준비 방법",
                "개발 서적 추천",
                "개발 환경 설정 도움"
        };

        for (String title : generalQnaTitles) {
            Member randomMember = getRandomMember();
            Post generalQnaPost = Post.builder()
                    .title(title)
                    .content("일반적인 질문입니다.\n\n경험담이나 조언 부탁드려요!")
                    .postType(PostType.FAQ)
                    .member(randomMember)
                    .board(generalQna)
                    .anonymous(random.nextBoolean())
                    .build();
            postService.save(generalQnaPost);
        }
    }

    private void addGalleryImages(Post post) {
        // 갤러리 이미지 3-5개씩 랜덤 추가
        int imageCount = 3 + random.nextInt(3);

        for (int i = 0; i < imageCount; i++) {
            Attachment attachment = Attachment.builder()
                    .url("https://picsum.photos/800/600?random=" + post.getId() + i)
                    .size(1024L * (500 + random.nextInt(500))) // 500KB-1MB
                    .fileType("image/jpeg")
                    .post(post)
                    .build();

            attachmentService.save(attachment);
        }
    }

    private void addCommentData() {
        log.info("댓글 테스트 데이터를 추가합니다...");

        // 모든 게시글 조회 (간단하게 일부만)
        // 실제로는 PostService에서 findAll 같은 메서드를 만들어야 하지만
        // 테스트를 위해 주요 게시판의 게시글들에만 댓글 추가

        Board freeBoard = boardService.findByName("자유 게시판");
        List<Post> freePosts = postService.findByBoardId(freeBoard.getId());

        // 자유게시판 게시글들에 댓글 추가
        for (Post post : freePosts) {
            addCommentsToPost(post, 2 + random.nextInt(4)); // 2-5개 댓글
        }

        Board devQna = boardService.findByName("개발 Q&A");
        List<Post> devPosts = postService.findByBoardId(devQna.getId());

        // 개발 Q&A 게시글들에 댓글 추가
        for (Post post : devPosts) {
            addCommentsToPost(post, 1 + random.nextInt(3)); // 1-3개 댓글
        }

        log.info("댓글 테스트 데이터 추가 완료");
    }

    private void addCommentsToPost(Post post, int commentCount) {
        String[] commentTexts = {
                "좋은 글 감사합니다!",
                "도움이 많이 되었어요.",
                "저도 같은 생각입니다.",
                "정말 유익한 정보네요!",
                "공감합니다!",
                "좋은 의견 감사해요.",
                "이런 정보를 찾고 있었는데 감사합니다.",
                "다음에도 좋은 글 부탁드려요!",
                "질문이 있는데 답변 가능할까요?",
                "추가 정보가 더 있다면 공유해주세요!"
        };

        for (int i = 0; i < commentCount; i++) {
            Member randomMember = getRandomMember();
            String content = commentTexts[random.nextInt(commentTexts.length)];

            Comment comment = Comment.createComment(
                    content,
                    random.nextBoolean(),
                    post,
                    randomMember
            );

            commentService.save(comment);

            // 일부 댓글에 대댓글 추가
            if (random.nextInt(100) < 30) { // 30% 확률로 대댓글
                Member replyMember = getRandomMember();
                Comment reply = Comment.createReply(
                        "답글입니다. " + content,
                        random.nextBoolean(),
                        post,
                        replyMember,
                        comment
                );
                commentService.save(reply);
            }
        }
    }

    private void addHeartData() {
        log.info("좋아요 테스트 데이터를 추가합니다...");

        // 게시글 좋아요는 PostHeartService를 통해 추가해야 함
        // 여기서는 랜덤하게 일부 게시글에 좋아요 추가

        Board freeBoard = boardService.findByName("자유 게시판");
        List<Post> freePosts = postService.findByBoardId(freeBoard.getId());

        for (Post post : freePosts) {
            // 랜덤하게 0-10개의 좋아요 추가
            int heartCount = random.nextInt(11);
            addHeartsToPost(post, heartCount);
        }

        log.info("좋아요 테스트 데이터 추가 완료");
    }

    private void addHeartsToPost(Post post, int heartCount) {
        for (int i = 0; i < heartCount; i++) {
            Member randomMember = getRandomMember();
            try {
                postHeartService.save(post.getId(), randomMember.getId());
            } catch (Exception e) {
                // 중복 좋아요 등의 예외는 무시
            }
        }
    }

    private void addBoardFavoriteData() {
        log.info("즐겨찾기 게시판 테스트 데이터를 추가합니다...");

        // 모든 회원이 2-5개의 게시판을 즐겨찾기에 추가
        for (int i = 1; i <= 30; i++) { // 30명의 회원
            try {
                String loginId;
                if (i == 1) {
                    loginId = "president2024";
                } else if (i == 2) {
                    loginId = "vice2024";
                } else if (i <= 12) {
                    loginId = "senior" + (i - 2);
                } else {
                    loginId = "junior" + (i - 12);
                }

                Member member = memberService.findByLoginId(loginId);
                addRandomFavorites(member, 2 + random.nextInt(4));
            } catch (Exception e) {
                // 회원이 없는 경우 무시
            }
        }

        log.info("즐겨찾기 게시판 테스트 데이터 추가 완료");
    }

    private void addRandomFavorites(Member member, int favoriteCount) {
        String[] boardNames = {
                "자유 게시판", "알고리즘 스터디", "웹 개발 스터디",
                "팀 프로젝트", "개발 Q&A", "활동 사진 갤러리"
        };

        for (int i = 0; i < favoriteCount; i++) {
            try {
                String boardName = boardNames[random.nextInt(boardNames.length)];
                Board board = boardService.findByName(boardName);
                boardFavoriteService.addFavorite(member, board);
            } catch (Exception e) {
                // 중복 즐겨찾기 등의 예외는 무시
            }
        }
    }

    private Member getRandomMember() {
        int memberNum = 1 + random.nextInt(30);

        if (memberNum == 1) {
            return memberService.findByLoginId("president2024");
        } else if (memberNum == 2) {
            return memberService.findByLoginId("vice2024");
        } else if (memberNum <= 12) {
            return memberService.findByLoginId("senior" + (memberNum - 2));
        } else {
            return memberService.findByLoginId("junior" + (memberNum - 12));
        }
    }
} 