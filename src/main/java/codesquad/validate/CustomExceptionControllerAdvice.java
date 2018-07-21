package codesquad.validate;

import codesquad.CannotDeleteException;
import codesquad.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class CustomExceptionControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(CustomExceptionControllerAdvice.class);

    @Resource(name = "messageSourceAccessor")
    private MessageSourceAccessor msa;

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCustomException(CustomException exception, Model model) {
        log.debug("@ControllerAdvice called");
        model.addAttribute("errors", exception.getMessage());
        return "/error";
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBindException(BindException e, Model model) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        log.debug("BindException {}", e.getMessage());
        List<String> list = new ArrayList<String>();
        for (ObjectError objectError : errors) {
            FieldError fieldError = (FieldError) objectError;
            model.addAttribute("errors", getErrorMessage(fieldError));
        }
        return "/error";
    }

    @ExceptionHandler(CannotDeleteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleCannotDeleteException(CannotDeleteException exception, Model model) {
        model.addAttribute("errors", exception.getMessage());
        return "/error";
    }

    private String getErrorMessage(FieldError fieldError) {
        Optional<String> code = getFirstCode(fieldError);
        if (!code.isPresent()) {
            return null;
        }

        String errorMessage = msa.getMessage(code.get(), fieldError.getArguments(), fieldError.getDefaultMessage());
        log.info("error message: {}", errorMessage);
        return errorMessage;
    }

    private Optional<String> getFirstCode(FieldError fieldError) {
        String[] codes = fieldError.getCodes();
        if (codes == null || codes.length == 0) {
            return Optional.empty();
        }
        return Optional.of(codes[0]);
    }
}
