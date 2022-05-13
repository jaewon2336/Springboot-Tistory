package site.metacoding.blogv3.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.blogv3.domain.Love.Love;
import site.metacoding.blogv3.domain.Love.LoveRepository;
import site.metacoding.blogv3.domain.category.Category;
import site.metacoding.blogv3.domain.category.CategoryRepository;
import site.metacoding.blogv3.domain.post.Post;
import site.metacoding.blogv3.domain.post.PostRepository;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.domain.visit.Visit;
import site.metacoding.blogv3.domain.visit.VisitRepository;
import site.metacoding.blogv3.handler.ex.CustomApiException;
import site.metacoding.blogv3.handler.ex.CustomException;
import site.metacoding.blogv3.util.UtilFileUpload;
import site.metacoding.blogv3.web.dto.post.PostDetailRespDto;
import site.metacoding.blogv3.web.dto.post.PostRespDto;
import site.metacoding.blogv3.web.dto.post.PostWriteReqDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    // private static final Logger LOGGER = LogManager.getLogger(PostService.class);

    @Value("${file.path}") // yml에 등록한 키 값 찾을 때 사용하는 어노테이션
    private String uploadFolder;

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final VisitRepository visitRepository;
    private final LoveRepository loveRepository;
    private final EntityManager em; // IoC 컨테이너에서 가져옴

    @Transactional
    public Love 좋아요(Integer postId, User principal) {

        // 숙제 : Love를 Dto에 옮겨서 비영속화된 데이터를 응답하기
        Post postEntity = postFindById(postId);
        Love love = new Love(); // 비영속상태
        love.setUser(principal);
        love.setPost(postEntity);

        return loveRepository.save(love);
    }

    @Transactional
    public void 좋아요취소(Integer loveId, User principal) {
        // 권한체크
        loveFindById(loveId);
        // 숙제 : 유저아이디랑 러브의 유저아이디랑 비교
        loveRepository.deleteById(loveId);
    }

    @Transactional
    public void 게시글삭제(Integer id, User principal) {

        // 게시글 찾기
        Post postEntity = postFindById(id);

        // 권한 체크
        boolean isAuth = authCheck(postEntity.getUser().getId(), principal.getId());

        if (isAuth) {
            // 삭제
            postRepository.deleteById(id);
        } else {
            throw new CustomException("삭제 권한이 없습니다.");
        }
    }

    @Transactional
    public PostDetailRespDto 게시글상세보기(Integer id) {

        PostDetailRespDto postDetailRespDto = new PostDetailRespDto();

        // 게시글 찾기
        Post postEntity = postFindById(id);

        // 방문자 카운트 증가
        visitIncrease(postEntity.getUser().getId());

        // 리턴값 만들기
        postDetailRespDto.setPost(postEntity);
        postDetailRespDto.setPageOwner(false);

        // 좋아요 유무 추가하기 (로그인 한 사람이 해당 게시글을 좋아하는지)
        postDetailRespDto.setLove(false);

        return postDetailRespDto;
    }

    @Transactional
    public PostDetailRespDto 게시글상세보기(Integer id, User principal) {

        PostDetailRespDto postDetailRespDto = new PostDetailRespDto();

        // 게시글 찾기
        Post postEntity = postFindById(id);

        // 권한체크
        boolean isAuth = authCheck(postEntity.getUser().getId(), principal.getId());

        // 방문자 카운트 증가
        visitIncrease(postEntity.getUser().getId());

        // 좋아요 유무 추가하기 (로그인 한 사람이 해당 게시글을 좋아하는지)
        // (1) 로그인 한 사람의 userId와 상세보기 한 postId로 Love 테이블에서 SELECT해서 row가 있으면 true
        Optional<Love> loveOp = loveRepository.mFindByUserIdAndPostId(principal.getId(), id);
        if (loveOp.isPresent()) {
            postDetailRespDto.setLove(true);
        } else {
            postDetailRespDto.setLove(false);
        }

        // 리턴값 만들기
        postDetailRespDto.setPost(postEntity);
        postDetailRespDto.setPageOwner(isAuth);

        return postDetailRespDto;
    }

    public List<Category> 게시글쓰기화면(User principal) {
        return categoryRepository.findByUserId(principal.getId());
    }

    // 서비스는 여러가지 로직이 공존한다. -> 단점 : 디버깅하기 힘들다.
    // 하나의 서비스는 여러가지 일을 한번에 처리한다.(여러가지 일이 하나의 트랜잭션이다.)
    @Transactional
    public void 게시글쓰기(PostWriteReqDto postWriteReqDto, User principal) {

        // 1. UUID로 파일쓰고 경로 리턴 받기
        String thumnail = null;
        if (!postWriteReqDto.getThumnailFile().isEmpty()) {
            thumnail = UtilFileUpload.write(uploadFolder, postWriteReqDto.getThumnailFile());
        }

        // 2. 카테고리 있는지 확인
        Optional<Category> categoryOp = categoryRepository.findById(postWriteReqDto.getCategoryId());

        // 3. 카테고리 있으면 post DB에 저장
        if (categoryOp.isPresent()) {
            Post post = postWriteReqDto.toEntity(thumnail, principal, categoryOp.get());
            postRepository.save(post);
        } else {
            throw new CustomException("해당 카테고리가 존재하지 않습니다.");
        }
    }

    @Transactional
    public PostRespDto 게시글목록보기(int pageOwnerId, Pageable pageable) {

        Page<Post> postsEntity = postRepository.findByUserId(pageOwnerId, pageable);
        List<Category> categoriesEntity = categoryRepository.findByUserId(pageOwnerId);

        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 0; i < postsEntity.getTotalPages(); i++) {
            pageNumbers.add(i);
        }

        // 방문자 카운터 증가
        Visit visitEntity = visitIncrease(pageOwnerId);

        PostRespDto postRespDto = new PostRespDto(
                postsEntity,
                categoriesEntity,
                pageOwnerId,
                postsEntity.getNumber() - 1,
                postsEntity.getNumber() + 1,
                pageNumbers,
                visitEntity.getTotalCount());

        return postRespDto;
    }

    @Transactional
    public PostRespDto 카테고리별게시글목록보기(int pageOwnerId, int categoryId, Pageable pageable) {

        Page<Post> postsEntity = postRepository.findByUserIdAndCategoryId(pageOwnerId, categoryId, pageable);
        List<Category> categoriesEntity = categoryRepository.findByUserId(pageOwnerId);

        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 0; i < postsEntity.getTotalPages(); i++) {
            pageNumbers.add(i);
        }

        // 방문자 카운터 증가
        Visit visitEntity = visitIncrease(pageOwnerId);

        PostRespDto postRespDto = new PostRespDto(
                postsEntity,
                categoriesEntity,
                pageOwnerId,
                postsEntity.getNumber() - 1,
                postsEntity.getNumber() + 1,
                pageNumbers,
                visitEntity.getTotalCount());

        return postRespDto;
    }

    // 중복되는 로직 메서드화

    // 아이디로 좋아요 한건 찾기
    private Love loveFindById(Integer loveId) {
        Optional<Love> loveOp = loveRepository.findById(loveId);
        if (loveOp.isPresent()) {
            Love loveEntity = loveOp.get();
            return loveEntity;
        } else {
            throw new CustomApiException("해당 좋아요가 존재하지 않습니다");
        }
    }

    // 아이디로 게시글 한건 찾기
    private Post postFindById(Integer postId) {
        Optional<Post> postOp = postRepository.findById(postId);
        if (postOp.isPresent()) {
            Post postEntity = postOp.get();
            return postEntity;
        } else {
            throw new CustomApiException("해당 게시글이 존재하지 않습니다");
        }
    }

    // 책임 : 로그인 유저가 게시글의 주인인지 확인.
    private boolean authCheck(Integer pageOwnerId, Integer principalId) {
        boolean isAuth = false;
        if (principalId == pageOwnerId) {
            isAuth = true;
        } else {
            isAuth = false;
        }
        return isAuth;
    }

    // 책임 : 방문자수 증가
    private Visit visitIncrease(Integer pageOwnerId) {
        Optional<Visit> visitOp = visitRepository.findById(pageOwnerId);
        if (visitOp.isPresent()) {
            Visit visitEntity = visitOp.get();
            Long totalCount = visitEntity.getTotalCount();
            visitEntity.setTotalCount(totalCount + 1);
            return visitEntity;
        } else {
            log.error("미친 심각", "회원가입할때 Visit이 안 만들어지는 심각한 오류가 있습니다.");
            // sms 메시지 전송
            // email 전송
            // file 쓰기
            throw new CustomException("일시적 문제가 생겼습니다. 관리자에게 문의해주세요.");
        }
    }

    ////////////////////////////////////// 연습////////////////////////////////////////////////

    // JPQL : Java Persistence Query Langauge
    public Post emTest1(int id) {
        em.getTransaction().begin(); // 트랜잭션 시작

        // 쿼리를 컴파일 시점에 오류 발견하기 위해 QueryDSL 사용
        String sql = null;

        // 동적 쿼리
        if (id == 1) {
            sql = "SELECT * FROM post WHERE id = 1";
        } else {
            sql = "SELECT * FROM post WHERE id = 2";
        }

        TypedQuery<Post> query = em.createQuery(sql, Post.class); // 내부적으로 영속화 됨
        Post postEntity = query.getSingleResult();

        try {
            // insert()

            // update()

            em.getTransaction().commit();
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
        }

        em.close(); // 트랜잭션 종료

        return postEntity; // 대신에 repository를 타지 않는다!
    }

    // 영속화 비영속화
    public Love emTest2() {
        Love love = new Love(); // 최초에는 remove 상태와 동일

        em.persist(love); // SELECT 하지 않아도 영속성 컨텍스트에 넣을 수 있음, 영속화
        em.detach(love); // 비영속화
        em.merge(love); // 재영속화
        em.remove(love); // 영속성 삭제
        return love; // MessageConverter가 getter 때릴 때도 상관없다. 비영속화시켰으니까!

        // 미리 필요한 애들을 다 땡겨내림 - Lazy loading 미리하기
        // Hibernate.initialize(love);
    }

    ////////////////////////////////////// 연습////////////////////////////////////////////////
}
