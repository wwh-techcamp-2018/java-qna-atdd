package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping
    public String list(Model model) {
        List<Question> questions = qnaService.findAll();
        log.debug("question size : {}", questions.size());
        model.addAttribute("questions", questions);
        return "/home";
    }

    @PostMapping
    public String create(@LoginUser User user, Question question) {
        qnaService.create(user, question);
        return "redirect:/questions";
    }

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "/qna/form";
    }

    @GetMapping("/{id:\\d+}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findQuestionById(id, loginUser).orElseThrow(UnAuthorizedException::new));
        return "/qna/updateForm";
    }

    @GetMapping("/{id:\\d+}")
    public String show(@PathVariable long id, Model model) {
        Question question = qnaService.findQuestionById(id).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @PutMapping("/{id:\\d+}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question target) {
        qnaService.update(loginUser, id, target);
        return "redirect:/questions";
    }

    @DeleteMapping("/{id:\\d+}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/questions";
    }

}
