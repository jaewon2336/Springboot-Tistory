package site.metacoding.blogv3.web;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import site.metacoding.blogv3.config.auth.LoginUser;
import site.metacoding.blogv3.service.UserService;
import site.metacoding.blogv3.util.UtilValid;
import site.metacoding.blogv3.web.dto.user.JoinReqDto;
import site.metacoding.blogv3.web.dto.user.PasswordResetReqDto;

@RequiredArgsConstructor
@Controller
public class UserController {

    // DI
    private final UserService userService;
    private final HttpSession session;

    @PutMapping("/s/api/user/profile-img")
    public ResponseEntity<?> profileImgUpdate(@AuthenticationPrincipal LoginUser loginUser,
            MultipartFile profileImgFile) {

        // 주소로 받은 id를 사용하면 세션값과 비교해 권한체크 해줘야하기 때문에
        // 더욱 안전하게 세션값을 사용
        userService.프로필사진수정하기(loginUser.getUser().getId(), profileImgFile, session);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/login-form")
    public String loginForm() {
        return "/user/loginForm";
    }

    @GetMapping("/join-form")
    public String joinForm() {
        return "/user/joinForm";
    }

    @GetMapping("/user/password-reset-form")
    public String passwordResetForm() {
        return "/user/passwordResetForm";
    }

    @PostMapping("/join")
    public String join(@Valid JoinReqDto joinReqDto, BindingResult bindingResult) {

        UtilValid.요청에러처리(bindingResult);

        // 핵심 로직
        userService.회원가입(joinReqDto.toEntity());

        return "redirect:login-form";
    }

    // ResponseEntity 는 @ResponseBody를 붙이지 않아도 data를 리턴한다.
    @GetMapping("/api/user/username-same-check")
    public ResponseEntity<?> usernameCheck(String username) {

        // 1. SELECT * FROM user WHERE username = :username
        boolean isNotSame = userService.유저네임중복체크(username); // true (같지 않다)

        return new ResponseEntity<>(isNotSame, HttpStatus.OK);
    }

    @PostMapping("/user/password-reset")
    public String passwordReset(@Valid PasswordResetReqDto passwordResetReqDto, BindingResult bindingResult) {

        UtilValid.요청에러처리(bindingResult);

        userService.패스워드초기화(passwordResetReqDto);

        return "redirect:/login-form";
    }

    @GetMapping("/s/user/{id}")
    public String updateForm(@PathVariable Integer id) {
        return "/user/updateForm";
    }
}
