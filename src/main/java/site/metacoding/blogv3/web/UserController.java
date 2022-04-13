package site.metacoding.blogv3.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.handler.ex.CustomException;
import site.metacoding.blogv3.service.UserService;
import site.metacoding.blogv3.web.dto.user.JoinReqDto;

@RequiredArgsConstructor
@Controller
public class UserController {

    // DI
    private final UserService userService;

    @GetMapping("/login-form")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/join-form")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("find-password")
    public String findPassword() {
        return "user/findPassword";
    }

    @PostMapping("/join")
    public String join(@Valid JoinReqDto joinReqDto, BindingResult bindingResult) {
        // 회원가입 로직에서 유효성 검사 코드는 부가적인 코드!! -> AOP
        if (bindingResult.hasErrors() == true) { // 하나라도 오류가 있다면 true
            Map<String, String> errorMap = new HashMap<>();

            for (FieldError fe : bindingResult.getFieldErrors()) {
                // System.out.println(fe.getField()); // 어느 변수에서 오류가 났는지 알려줌
                // System.out.println(fe.getDefaultMessage()); // 메세지 지정안해줘도 디폴트 메세지가 있음

                errorMap.put(fe.getField(), fe.getDefaultMessage());
            }

            throw new CustomException(errorMap.toString());
        }

        // 핵심 로직
        userService.회원가입(joinReqDto.toEntity());

        return "redirect:login-form";
    }

    @GetMapping("/api/user/username/same-check")
    public @ResponseBody ResponseEntity<?> usernameCheck(String username) {

        // 1. SELECT * FROM user WHERE username = :username
        Optional<User> userEntity = userService.아이디중복체크(username);

        if (userEntity.isEmpty()) {
            return new ResponseEntity<>(1, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(-1, HttpStatus.OK);
        }

    }

    @PutMapping("/api/send-mail")
    public @ResponseBody ResponseEntity<?> passwordReset(@RequestParam String username) {

        System.out.println("username : -------------------------" + username);

        // 1. SELECT * FROM user WHERE username = :username
        Optional<User> userOp = userService.아이디중복체크(username);

        if (userOp.isPresent()) {

            // 비밀번호 초기화 update!!
            userService.비밀번호변경(userOp.get());

            return new ResponseEntity<>(1, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(-1, HttpStatus.OK);
        }
    }
}
