package site.metacoding.blogv3.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import site.metacoding.blogv3.domain.category.Category;
import site.metacoding.blogv3.domain.category.CategoryRepository;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.domain.user.UserRepository;

@Profile("test")
@Configuration
public class DBInitializer {

    @Bean
    public CommandLineRunner demo(UserRepository userRepository, CategoryRepository categoryRepository) {

        return (args) -> {
            User principal = User.builder()
                    .username("ssar")
                    .password("1234")
                    .email("xldzjqpf1588@naver.com")
                    .build();

            userRepository.save(principal);

            Category category = Category.builder()
                    .title("스프링특강")
                    .user(principal)
                    .build();

            categoryRepository.save(category);
        };
    }
}