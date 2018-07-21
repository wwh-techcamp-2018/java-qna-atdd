package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.CustomException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QnaService qnaService;

    @GetMapping("/{id}")
    public String showQuestion(@PathVariable long id, Model model) {
        Question question = qnaService.findById(id).orElseThrow(() ->new CustomException("Doesn't Exist Question"));
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser){
        return "/qna/form";
    }

    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    @PostMapping("")
    public String create(@LoginUser User loginUser, @Valid Question question){//, BindingResult bindingResult){
//        if(bindingResult.hasErrors()){
//            return  "/error";
//        }
        log.debug("QuestionController_create : {}", question.toString());
        qnaService.create(loginUser,question);
        return "redirect:/";
    }

    @GetMapping("/form/{id}")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model){
        log.debug("Question {}",qnaService.findById(id));
        log.debug("LoginUser {}",loginUser);
        model.addAttribute("question", qnaService.matchWriter(id, loginUser));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, @Valid Question question){
        qnaService.update(loginUser, id, question);
        return "redirect:/questions/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id );
        return "redirect:/";
    }

}
