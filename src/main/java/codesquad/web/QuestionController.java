package codesquad.web;

import codesquad.CustomException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @PostMapping("")
    public String create(@LoginUser User loginUser, @Valid Question question){
        log.debug("QuestionController_create : {}"+question.toString());
        qnaService.create(loginUser,question);
        return "redirect:/";
    }

}
