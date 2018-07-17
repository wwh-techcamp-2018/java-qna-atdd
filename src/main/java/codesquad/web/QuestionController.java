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

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        Question returnedQuestion = qnaService.create(loginUser, question);
        return "redirect:" + returnedQuestion.generateUrl();
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("question", qnaService.findQuestionById(id));
        return "qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        model.addAttribute("question", qnaService.findQuestionByIdAndUser(id, loginUser));
        return "qna/updateForm";
    }

    @PostMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable Long id, Question updateQuestion) {
        Question question = qnaService.update(loginUser, id, updateQuestion);
        return "redirect:" + question.generateUrl();
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.delete(loginUser, id);
        return "redirect:/";
    }
}
