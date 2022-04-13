package site.metacoding.blogv3.web;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import site.metacoding.blogv3.config.auth.LoginUser;

@Controller
public class MainController {

    @GetMapping({ "/" })
    public String main(@AuthenticationPrincipal LoginUser LoginUser) { // 시큐리티의 세션에 바로 접근
        // System.out.println(LoginUser.getUsername());
        // System.out.println(LoginUser.getUser().getUsername());

        // LoginUser lu = (LoginUser)
        // SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //
        // UserDetails
        // System.out.println(lu.getUser().getEmail());

        return "main";
    }
}
