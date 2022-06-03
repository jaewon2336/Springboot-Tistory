package site.metacoding.blogv3.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
public class UserRepositoryTest {
    // 아무것도 의존하는게 없기 때문에 stub이 필요없음

    @Autowired
    private UserRepository userRepository; // @DataJpaTest가 메모리에 띄워줌

    @Test
    @Order(1)
    public void save_테스트() {
        // given
        String username = "ssar";
        String password = "1234"; // 해시로 암호화하는것은 서비스 책임
        String email = "ssar@nate.com";
        LocalDateTime createDate = LocalDateTime.now();
        LocalDateTime updateDate = LocalDateTime.now();

        User user = new User(null, username, password, email, null, createDate, updateDate);

        // when
        User userEntity = userRepository.save(user);

        // then
        assertEquals(username, userEntity.getUsername());
    }

    @Test
    @Order(2)
    public void findByUsername_테스트() {
        // given
        String username = "ssar";

        // when
        Optional<User> userOp = userRepository.findByUsername(username);

        if (userOp.isPresent()) {
            User user = userOp.get();

            // then
            assertEquals(username, user.getUsername());
        }

    }

    @Test
    @Order(3)
    public void findById_테스트() {
        // given

        // when

        // then
    }

    @Test
    @Order(4)
    public void findByUsernameAndEmail_테스트() {
        // given

        // when

        // then
    }

}
