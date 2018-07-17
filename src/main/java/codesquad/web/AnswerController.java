package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/questions/{qId}/answer")
@Controller
public class AnswerController {

    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public String create(@PathVariable Long qId, @LoginUser User user, Answer answer) {
        qnaService.addAnswer(user, qId, answer.getContents());
        return "redirect:/questions/{qId}";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long qId, @PathVariable Long id, @LoginUser User user) throws CannotDeleteException {
        qnaService.deleteAnswer(user, id);
        return "redirect:/questions/{qId}";
    }

}
