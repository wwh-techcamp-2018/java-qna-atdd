package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.security.acl.NotOwnerException;

@RequestMapping("/questions")
@Controller
public class QuestionController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("")
    public String show() {
        return "/qna/show";
    }

    @GetMapping("/{qId}")
    public String showDetail(@PathVariable Long qId, Model model) throws EntityNotFoundException {
        Question q = qnaService.findById(qId).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", q);
        return "/qna/show";
    }

    @PostMapping("")
    public String create(@LoginUser User user, Question question) {
        Question q = qnaService.create(user, question);
        return "redirect:/questions/" + q.getId();
    }

    @PutMapping("/{qId}")
    public String update(@PathVariable Long qId, @LoginUser User user, Question question) throws NotOwnerException {
        Question q = qnaService.update(user, qId, question);
        return "redirect:/questions/" + q.getId();
    }

    @DeleteMapping("/{qId}")
    public String delete(@PathVariable Long qId, @LoginUser User user) throws CannotDeleteException {
        qnaService.deleteQuestion(user, qId);
        return "redirect:/questions";
    }
}
