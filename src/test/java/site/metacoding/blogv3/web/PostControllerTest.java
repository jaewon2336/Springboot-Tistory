package site.metacoding.blogv3.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import site.metacoding.blogv3.web.dto.post.PostWriteReqDto;

@ActiveProfiles("dev")
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

    @WithMockUser
    @Test
    public void write_테스트() throws Exception {
        // given
        PostWriteReqDto postWriteReqDto = PostWriteReqDto.builder()
                .categoryId(1) // 이거 분명히 터짐 FK 연결 안되어있어서!
                .title("스프링1강")
                .content("재밌음")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/post", postWriteReqDto).contentType(MediaType.));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print());
    }

    public void writeForm_테스트() {
    }

    public void postList_테스트() {
    }
}
