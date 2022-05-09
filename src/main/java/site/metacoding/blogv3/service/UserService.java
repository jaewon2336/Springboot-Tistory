package site.metacoding.blogv3.service;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.domain.user.UserRepository;
import site.metacoding.blogv3.domain.visit.Visit;
import site.metacoding.blogv3.domain.visit.VisitRepository;
import site.metacoding.blogv3.handler.ex.CustomApiException;
import site.metacoding.blogv3.util.UtilFileUpload;
import site.metacoding.blogv3.util.email.EmailUtil;
import site.metacoding.blogv3.web.dto.user.PasswordResetReqDto;

@RequiredArgsConstructor
@Service // IoC 등록
public class UserService {

    @Value("${file.path}") // yml에 등록한 키 값 찾을 때 사용하는 어노테이션
    private String uploadFolder;

    // DI
    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailUtil emailUtil;

    @Transactional
    public void 프로필사진수정하기(Integer userId, MultipartFile profileImgFile, HttpSession session) {

        // 1. 유저 확인
        Optional<User> userOp = userRepository.findById(userId);
        if (userOp.isPresent()) {
            User userEntity = userOp.get();

            // 2. UUID로 파일 쓰고 경로 리턴 받기
            String profileImg = UtilFileUpload.write(uploadFolder, profileImgFile);

            // 3. user의 profileImg 변경
            userEntity.setProfileImg(profileImg);

            // 4. 세션값 변경
            session.setAttribute("principal", userEntity);

        } else {
            throw new CustomApiException("존재하지 않는 사용자입니다.");
        }
    }

    @Transactional
    public void 회원가입(User user) {
        // 1. save 한번
        String rawPassword = user.getPassword(); // 1234
        // 시큐리티가 해시로 암호화 안되어있으면 로그인에 실패 -> 시큐리티에게 암호화 알고리즘 알려주는 것 : IoC 등록
        String encPassword = bCryptPasswordEncoder.encode(rawPassword); // 해시 알고리즘
        user.setPassword(encPassword);

        User userEntity = userRepository.save(user);

        // 2. save 두번
        Visit visit = new Visit();
        visit.setTotalCount(0L);
        visit.setUser(userEntity); // 터뜨리고 테스트 해보기
        visitRepository.save(visit);
    }

    public boolean 유저네임중복체크(String username) {
        Optional<User> userOp = userRepository.findByUsername(username);

        if (userOp.isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    @Transactional
    public void 패스워드초기화(PasswordResetReqDto passwordResetReqDto) {
        // 1. username, email이 같은 것이 있는지 체크 (DB)
        Optional<User> userOp = userRepository.findByUsernameAndEmail(
                passwordResetReqDto.getUsername(),
                passwordResetReqDto.getEmail());

        // 2. 같은게 있다면 DB password 초기화 - BCrypt 암호화 - update (DB)
        if (userOp.isPresent()) {
            User userEntity = userOp.get(); // 영속화
            String encPassword = bCryptPasswordEncoder.encode("9999"); // 해시 알고리즘
            userEntity.setPassword(encPassword);

            // 3. 초기화 된 비밀번호 이메일로 전송
            emailUtil.sendEmail(userEntity.getEmail(), "블로그 비밀번호 초기화", "초기화 된 비밀번호 : 9999");
        } else {
            throw new RuntimeException("해당 정보가 존재하지 않습니다.");
        }
    }
}