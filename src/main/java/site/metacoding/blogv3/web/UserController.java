package site.metacoding.blogv3.web;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
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
}
