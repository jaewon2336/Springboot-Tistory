package site.metacoding.blogv3.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import site.metacoding.blogv3.web.dto.post.PostWriteReqDto;

@ActiveProfiles("test") // h2 디비로 변경!!!
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PostControllerTest {

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

    public void love_테스트() {
    }

    public void unLove_테스트() {
    }

    public void postDelete_테스트() {
    }

    public void detail_테스트() {
    }

    // WithMockUser는 간단한 인증만 통과하고 싶을 때 사용
    // 만약에 세션에 있는 값을 내부에서 사용해야 한다면 다른 것을 사용해야 함
    // @WithMockUser // username="username" password="password"
    @WithUserDetails("ssar")
    @Test
    public void write_테스트() throws Exception {
        // given

        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        PostWriteReqDto postWriteReqDto = PostWriteReqDto.builder()
                .categoryId(1) // 이거 분명히 터짐 FK 연결 안되어있어서!
                .title("스프링1강")
                .content("재밌음")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/s/post")
                        .param("title", postWriteReqDto.getTitle())
                        .param("content", postWriteReqDto.getContent())
                        .param("categoryId", postWriteReqDto.getCategoryId() + ""));

        // then
        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    public void writeForm_테스트() {
    }

    public void postList_테스트() {
    }
}
