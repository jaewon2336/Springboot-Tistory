package site.metacoding.blogv3.domain.post;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository; // @DataJpaTest가 메모리에 띄워줌

    public void mSave_테스트() {
    }

    @Test
    @Order(1)
    public void save_테스트() {
        // given title, content, thumnail, user, category, createDate, updateDate
        String title = "제목1";
        String content = "내용1";

        // when

        // then
    }

    public void findById_테스트() {
    }

    public void findByUserId_테스트() {
    }

    public void findByUserIdAndCategoryId_테스트() {
    }

    public void mFindByPopular_테스트() {
    }

    public void deleteById_테스트() {
    }

}
