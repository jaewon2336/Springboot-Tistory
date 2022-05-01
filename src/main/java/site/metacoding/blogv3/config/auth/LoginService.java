package site.metacoding.blogv3.config.auth;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.domain.user.UserRepository;

@RequiredArgsConstructor
@Service // IoC 컨테이너에 등록됨
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // user가 DB에 있으면 리턴!
        Optional<User> userOp = userRepository.findByUsername(username);

        if (userOp.isPresent()) {
            return new LoginUser(userOp.get()); // user만 뽑아서 세션에 넣어줄거야
        } // else {
          // 여기 어차피 타지도 않고 터져버린다!! 내가 제어 못함
          // throw new CustomException("유저네임 없어");
          // }

        return null;
    }
}