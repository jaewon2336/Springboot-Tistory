package site.metacoding.blogv3.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class PostController {

    @GetMapping("/user/{id}/post")
    public String postList(@PathVariable Integer id) {
        return "/post/list";
    }

}
