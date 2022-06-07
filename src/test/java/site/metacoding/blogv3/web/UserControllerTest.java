package site.metacoding.blogv3.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import site.metacoding.blogv3.domain.user.User;

// RestController 테스트는 통합테스트로 하면 편하다 -> TestRestTemplate
// Controller 테스트는 모델 값 확인이 안되기 때문에 WebMvcTest 사용해야함 MockMvc 필요
// WebMvcTest, SpringbootTest 둘 중에 무엇을 쓸지는 메모리에 무엇을 올릴지에 따라 다르다.

// SpringbootTest + MockMvc -> 메모리에 다 올림
// MockMvcTest + MockMvc -> 컨트롤러 앞단이 메모리에 올라옴
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

    public void join_테스트() {
    }

    public void profileImgUpdate_테스트() {
    }

    public void findPassword_테스트() {
    }

    @Test
    public void usernameCheck_테스트() {
        // given

        // when

        // then
    }

    public void passwordReset_테스트() {
    }

}
