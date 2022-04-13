package site.metacoding.blogv3.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class EmailController {

    private final EmailUtil emailUtil;

    @GetMapping("/sendmail")
    public String sendMail() {
        emailUtil.sendEmail("xldzjqpf1588@naver.com", "티스토리 비밀번호 초기화", "초기화 된 비밀번호 : 9999");
        return "메일 잘 보내졌어";
    }
}
