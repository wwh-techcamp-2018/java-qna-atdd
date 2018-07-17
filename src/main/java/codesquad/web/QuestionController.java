package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.exception.CannotDeleteException;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable Long id, Question updatedQuestion) {
        qnaService.update(loginUser, id, updatedQuestion);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }

    @PostMapping("/{questionId}/answers")
    public String addAnswer(@LoginUser User loginUser, @PathVariable Long questionId, String contents) {
        qnaService.addAnswer(loginUser, questionId, contents);
        return "redirect:/questions/" + questionId;
    }

    @DeleteMapping("{questionId}/answers/{answerId}")
    public String deleteAnswer(@LoginUser User loginUser, @PathVariable Long questionId, @PathVariable Long answerId) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, answerId);
        return "redirect:/questions/" + questionId;
    }

    @GetMapping("/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }
}
