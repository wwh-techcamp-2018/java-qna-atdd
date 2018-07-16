package codesquad.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(UnAuthenticationException.class)
    public String unAuthentication() {
        return "/user/login_failed";
    }
}
