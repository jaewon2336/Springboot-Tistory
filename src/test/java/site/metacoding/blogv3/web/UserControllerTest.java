package site.metacoding.blogv3.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import site.metacoding.blogv3.domain.user.User;

// RestController 테스트는 통합테스트로 하면 편하다 -> TestRestTemplate
// Controller 테스트는 모델 값 확인이 안되기 때문에 WebMvcTest 사용해야함 MockMvc 필요
// WebMvcTest, SpringbootTest 둘 중에 무엇을 쓸지는 메모리에 무엇을 올릴지에 따라 다르다.

// SpringbootTest + MockMvc -> 메모리에 다 올림
// MockMvcTest + MockMvc -> 컨트롤러 앞단이 메모리에 올라옴
@ActiveProfiles("test") // test 설정파일로 실행 -> h2
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @AutoConfigureMockMvc // MockMvc를 IoC에 띄워라
public class UserControllerTest {

    // @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context) // spring의 환경을 알고있어야 set할텐데
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void joinForm_테스트() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/join-form"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));

        // ResponseEntity<String> responseEntity =
        // testRestTemplate.exchange("/join-form", HttpMethod.GET, null,
        // String.class);

        // assertEquals(200, responseEntity.getStatusCodeValue());
        // assertEquals("text/html;charset=UTF-8",
        // responseEntity.getHeaders().getContentType().toString());
    }

    @Test
    public void loginForm_테스트() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/login-form"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void passwordResetForm_테스트() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/user/password-reset-form"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @WithMockUser // 가짜 인증 객체 -> 시큐리티 통과 위해서
    @Test
    public void updateForm_테스트() throws Exception {
        // given
        Integer id = 1;

        User principal = User.builder()
                .id(1)
                .username("ssar")
                .password("1234")
                .email("ssar@nate.com")
                .profileImg(null)
                .build();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("principal", principal);

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user/" + id).session(session));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void join_테스트() {
        assertEquals("1", "1");
    }

    @WithUserDetails("ssar")
    @Test
    public void profileImgUpdate_테스트() throws Exception {
        // 세션에 접근하기
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // System.out.println(authentication.isAuthenticated()); // 로그인 되어있냐?
        // System.out.println(authentication.getName()); // username
        // System.out.println(authentication.getCredentials()); // password
        // System.out.println(authentication.getPrincipal()); // LoginUser
        // LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // given
        File file = new File(
                "C:\\workspace\\repositories\\spring_lab\\blogv3\\src\\main\\resources\\static\\images\\dog.jpg");

        MockMultipartFile image = new MockMultipartFile("profileImgFile", "dog.jpg", "image/jpeg",
                Files.readAllBytes(file.toPath()));

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/s/api/user/profile-img");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        // when
        ResultActions resultActions = mockMvc.perform(
                builder.file(image));

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void usernameCheck_테스트() {
        assertEquals("1", "1");
    }

    @Test
    public void passwordReset_테스트() throws Exception {
        // assertEquals("1", "1");

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/user/password-reset")
                .param("username", "ssar")
                .param("email", "xldzjqpf1588@naver.com") // 실제 초기화 된 비밀번호 받을 이메일
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

}
