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
    public String showCreateForm(@LoginUser(required = false) User user) {
        if (user.isGuestUser()) {
            return "/qna/unauthenticated";
        }

        return "/qna/form";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser(required = false) User user, @PathVariable long id) throws CannotDeleteException {
        if (user.isGuestUser()) {
            return "/qna/unauthenticated";
        }

        try {
            qnaService.deleteQuestion(user, id);
            return "redirect:/";
        } catch (UnAuthorizedException ex) {
            return "/qna/unauthorized";
        }
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser(required = false) User user, @PathVariable long id, Model model) {
        if (user.isGuestUser()) {
            return "/qna/unauthenticated";
        }

        Question question = qnaService.findById(id, user).orElse(null);

        if (question == null) {
            return "/qna/unauthorized";
        }

        model.addAttribute("question", question);

        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser(required = false) User user, @PathVariable long id, @Valid Question updatedQuestion) {
        if (user.isGuestUser()) {
            return "/qna/unauthenticated";
        }

        try {
            Question question = qnaService.update(user, id, updatedQuestion);
            return "redirect:" + question.generateUrl();
        } catch (UnAuthorizedException ex) {
            return "/qna/unauthorized";
        }

    }
}
