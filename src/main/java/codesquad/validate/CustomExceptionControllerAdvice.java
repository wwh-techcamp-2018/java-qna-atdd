package codesquad.validate;

import codesquad.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class CustomExceptionControllerAdvice {
 private static final Logger log = LoggerFactory.getLogger(CustomExceptionControllerAdvice.class);
    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCustomException(CustomException exception, Model model) {
        log.debug("@ControllerAdvice called");
        model.addAttribute("error", exception);
        return "/error";
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class
    })

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(MethodArgumentNotValidException exception, Model model) {
        List<ObjectError> errors = exception.getBindingResult().getAllErrors();
        StringBuilder response = new StringBuilder();
        log.debug("@Valid : {}",exception.getMessage());
        model.addAttribute("error", exception);
        return "/error";
    }
}
