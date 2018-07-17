package codesquad.web;

import codesquad.CannotDeleteException;
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

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        Question createdQuestion = qnaService.create(loginUser, question);
        return String.format("redirect:/questions/%d", createdQuestion.getId());
    }

    @GetMapping("/{questionId}")
    public String show(@PathVariable long questionId, Model model) {
        Question question = qnaService.findById(questionId)
                .orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("")
    public String list(Model model) {
        return "redirect:/";
    }

    @GetMapping("/{questionId}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long questionId, Model model) {
        Question question = qnaService.findById(questionId).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{questionId}")
    public String update(@LoginUser User loginUser, @PathVariable long questionId, Question target) {
        Question question = qnaService.update(loginUser, questionId, target);
        return String.format("redirect:/questions/%d", question.getId());
    }

    @DeleteMapping("/{questionId}")
    public String delete(@LoginUser User loginUser, @PathVariable long questionId) {
        try {
            qnaService.deleteQuestion(loginUser, questionId);
        } catch (CannotDeleteException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
}
