package site.metacoding.blogv3.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.domain.user.UserRepository;
import site.metacoding.blogv3.domain.visit.Visit;
import site.metacoding.blogv3.domain.visit.VisitRepository;

@ExtendWith(MockitoExtension.class) // Mockito 컨테이너 생성
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    // Controller, Filter, Repository 같은건 무거우니까 가짜로 띄운다.
    // BCryptPasswordEncoder 클래스 하나는 (무겁지 않은것) @Spy로 주입하면 된다.
    // 레이어가 아닌 것들! (컨트롤러, 서비스, 레파지토리)
    @Spy
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;

    @Mock
    private VisitRepository visitRepository;

    // findByUsername을 호출하는 stub을 정의할건데 이건 테스트 필요없겠다. 레파지토리에서 테스트해라.
    public void 유저네임중복체크() {
        // given

        // stub

        // when

        // then
    }

    @Test
    public void 회원가입_테스트() {
        // given 1
        User givenUser = User.builder()
                .username("ssar")
                .password("1234")
                .email("ssar@nate.com")
                .build();

        // stub 1
        User mockUserEntity = User.builder()
                .id(1)
                .username("ssar")
                .password("1234")
                .email("ssar@nate.com")
                .profileImg(null)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Mockito.when(userRepository.save(givenUser)).thenReturn(mockUserEntity);

        // given 2
        Visit givenVisit = Visit.builder()
                .totalCount(0L)
                .user(mockUserEntity)
                .build();

        // stub 2
        Visit visitEntity = Visit.builder()
                .id(1)
                .totalCount(0L)
                .user(mockUserEntity) // 연결
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Mockito.when(visitRepository.save(givenVisit)).thenReturn(visitEntity);

        // when 실세 서비스 호출
        User userEntity = userService.회원가입(givenUser);

        // then
        assertEquals(givenUser.getEmail(), userEntity.getEmail());
    }

    public void 프로필사진수정하기_테스트() {

    }

    public void 패스워드초기화() {

    }

}
