package site.metacoding.blogv3.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import site.metacoding.blogv3.handler.ex.CustomException;

public class UtilValid {

    public static void 요청에러처리(BindingResult bindingResult) {
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
    }
}
