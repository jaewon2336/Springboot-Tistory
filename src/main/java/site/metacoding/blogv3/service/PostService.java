package site.metacoding.blogv3.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.blogv3.domain.category.Category;
import site.metacoding.blogv3.domain.category.CategoryRepository;
import site.metacoding.blogv3.domain.post.Post;
import site.metacoding.blogv3.domain.post.PostRepository;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.domain.user.UserRepository;
import site.metacoding.blogv3.domain.visit.Visit;
import site.metacoding.blogv3.domain.visit.VisitRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public void 게시글삭제(Integer id, User principal) {
        Optional<Post> postOp = postRepository.findById(id);

        if (postOp.isPresent()) {
            Post postEntity = postOp.get();

            // 권한 체크
            if (principal.getId() == postEntity.getUser().getId()) {
                // 삭제
                postRepository.deleteById(id);
            } else {
                throw new CustomException("삭제 권한이 없습니다.");
            }
        } else {
            throw new CustomException("해당 게시글이 존재하지 않습니다.");
        }
    }

    @Transactional
    public PostDetailRespDto 게시글상세보기(Integer id) {
        PostDetailRespDto postDetailRespDto = new PostDetailRespDto();

        Optional<Post> postOp = postRepository.findById(id);

        if (postOp.isPresent()) {

            Post postEntity = postOp.get();
            postDetailRespDto.setPost(postEntity);
            postDetailRespDto.setPageOwner(false);

            // 방문자 카운트 증가
            Optional<Visit> visitOp = visitRepository.findById(postEntity.getUser().getId());

            if (visitOp.isPresent()) {
                Visit visitEntity = visitOp.get();
                Long totalCount = visitEntity.getTotalCount();
                visitEntity.setTotalCount(totalCount + 1);
            } else {
                log.error("미친 심각", "회원가입할 때 visit이 안만들어지는 심각한 오류가 있습니다.");
                // sms 메시지, email 전송, file 쓰기
                throw new CustomException("일시적 문제가 생겼습니다. 관리자에게 문의해주세요.");
            }
            return postDetailRespDto;
        } else {
            throw new CustomException("해당 게시글을 찾을 수 없습니다.");
        }
    }

    @Transactional
    public PostDetailRespDto 게시글상세보기(Integer id, User principal) {

        PostDetailRespDto postDetailRespDto = new PostDetailRespDto();

        // 해당 페이지의 postId를 찾아서
        Integer postId = id;

        // 해당 페이지의 주인 userId가 무엇인지 알아야함
        Integer pageOwnerId = null;

        // 로그인한 사용자의 userId를 알아야함
        Integer loginUserId = principal.getId();

        Optional<Post> postOp = postRepository.findById(id);

        if (postOp.isPresent()) {

            Post postEntity = postOp.get();
            postDetailRespDto.setPost(postEntity);

            pageOwnerId = postEntity.getUser().getId();

            // 두 값을 비교해서 동일하면 isPageOwner에 true 추가
            if (pageOwnerId == loginUserId) {
                postDetailRespDto.setPageOwner(true);
            } else {
                postDetailRespDto.setPageOwner(false);
            }

            // 방문자 카운트 증가
            Optional<Visit> visitOp = visitRepository.findById(postEntity.getUser().getId());

            if (visitOp.isPresent()) {
                Visit visitEntity = visitOp.get();
                Long totalCount = visitEntity.getTotalCount();
                visitEntity.setTotalCount(totalCount + 1);
            } else {
                log.error("미친 심각", "회원가입할 때 visit이 안만들어지는 심각한 오류가 있습니다.");
                // sms 메시지, email 전송, file 쓰기
                throw new CustomException("일시적 문제가 생겼습니다. 관리자에게 문의해주세요.");
            }
            return postDetailRespDto;
        } else {
            throw new CustomException("해당 게시글을 찾을 수 없습니다.");
        }
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

        PostRespDto postRespDto = new PostRespDto(
                postsEntity,
                categoriesEntity,
                pageOwnerId,
                postsEntity.getNumber() - 1,
                postsEntity.getNumber() + 1,
                pageNumbers,
                0L);

        // 방문자 카운터 증가
        Optional<User> pageOwnerOp = userRepository.findById(pageOwnerId);

        if (pageOwnerOp.isPresent()) {
            User pageOwnerEntity = pageOwnerOp.get();
            Optional<Visit> visitOp = visitRepository.findById(pageOwnerEntity.getId());
            if (visitOp.isPresent()) {
                Visit visitEntity = visitOp.get();

                // dto에 방문자 수 담기 (request에서 ip 주소 받아서 동일하면 증가시키지 않는 로직이 필요함)
                postRespDto.setTotalCount(visitEntity.getTotalCount());

                Long totalCount = visitEntity.getTotalCount();
                visitEntity.setTotalCount(totalCount + 1);
            } else {
                log.error("미친 심각", "회원가입할때 Visit이 안 만들어지는 심각한 오류가 있습니다.");
                // sms 메시지 전송
                // email 전송
                // file 쓰기
                throw new CustomException("일시적 문제가 생겼습니다. 관리자에게 문의해주세요.");
            }
        } else {
            throw new CustomException("해당 블로그는 없는 페이지입니다.");
        }

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

        PostRespDto postRespDto = new PostRespDto(
                postsEntity,
                categoriesEntity,
                pageOwnerId,
                postsEntity.getNumber() - 1,
                postsEntity.getNumber() + 1,
                pageNumbers,
                0L);

        // 방문자 카운터 증가
        Optional<User> pageOwnerOp = userRepository.findById(pageOwnerId);

        if (pageOwnerOp.isPresent()) {
            User pageOwnerEntity = pageOwnerOp.get();
            Optional<Visit> visitOp = visitRepository.findById(pageOwnerEntity.getId());
            if (visitOp.isPresent()) {
                Visit visitEntity = visitOp.get();

                // dto에 방문자 수 담기 (request에서 ip 주소 받아서 동일하면 증가시키지 않는 로직이 필요함)
                postRespDto.setTotalCount(visitEntity.getTotalCount());

                Long totalCount = visitEntity.getTotalCount();
                visitEntity.setTotalCount(totalCount + 1);
            } else {
                log.error("미친 심각", "회원가입할때 Visit이 안 만들어지는 심각한 오류가 있습니다.");
                // sms 메시지 전송
                // email 전송
                // file 쓰기
                throw new CustomException("일시적 문제가 생겼습니다. 관리자에게 문의해주세요.");
            }
        } else {
            throw new CustomException("해당 블로그는 없는 페이지입니다.");
        }

        return postRespDto;
    }
}
