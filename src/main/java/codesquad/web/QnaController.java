package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/questions")
public class QnaController {

    @Autowired
    private QnaService qnaService;


    @PostMapping("")
    public String create(@LoginUser User user, @Valid Question question) {
        Question savedQuestion = qnaService.create(user, question);
        return "redirect:/questions/" + savedQuestion.getId();
    }

    @GetMapping("/form")
    public String showCreateForm(@LoginUser User user) {
        return "/qna/form";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User user, @PathVariable long id) throws CannotDeleteException {
        try {
            qnaService.deleteQuestion(user, id);
            return "redirect:/";
        } catch (UnAuthorizedException ex) {
            return "/unauthorized";
        }
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User user, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id, user);

        if (question == null) {
            return "/unauthorized";
        }

        model.addAttribute("question", question);

        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User user, @PathVariable long id, @Valid Question updatedQuestion) {
        try {
            Question question = qnaService.update(user, id, updatedQuestion);
            return "redirect:" + question.generateUrl();
        } catch (UnAuthorizedException ex) {
            return "/unauthorized";
        }

    }
}
