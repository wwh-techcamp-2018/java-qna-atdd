package codesquad.security;

import codesquad.LoginFailException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class SecurityControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(SecurityControllerAdvice.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void emptyResultData() {
        log.debug("EntityNotFoundException is happened!");
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public String unAuthorized() {
        log.debug("UnAuthorizedException is happened!");
        return "redirect:/";
    }

    @ExceptionHandler(UnAuthenticationException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public String unAuthentication() {
        log.debug("UnAuthenticationException is happened!");
        return "/user/login";
    }

    @ExceptionHandler(LoginFailException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public String loginFailed() {
        log.debug("LoginFailedException is happened!");
        return "/user/login_failed";
    }
}
