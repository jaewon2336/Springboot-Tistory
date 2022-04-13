package site.metacoding.blogv3.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.domain.user.UserRepository;

@RequiredArgsConstructor
@Service // IoC 등록
public class UserService {

    // DI
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void 회원가입(User user) {
        String rawPassword = user.getPassword(); // 1234
        // 시큐리티가 해시로 암호화 안되어있으면 로그인에 실패 -> 시큐리티에게 암호화 알고리즘 알려주는 것 : IoC 등록
        String encPassword = bCryptPasswordEncoder.encode(rawPassword); // 해시 알고리즘

        user.setPassword(encPassword);

        userRepository.save(user);
    }

}
