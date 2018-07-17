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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QnaService questionService;

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser) {
        return "/qna/form";
    }


    @GetMapping("{id}/form")
    public String updateForm(@LoginUser User loginUser) {
        return "/qna/updateForm";
    }

    @GetMapping("/{id:\\d+}")
    public String showQuestionDetail(@PathVariable Long id, Model model) {
        Optional<Question> maybeQuestion = questionService.findById(id);
        if (!maybeQuestion.isPresent()) {
            return "redirect:/";
        }

        model.addAttribute("question", maybeQuestion.get());
        return "/qna/show";
    }


    @PostMapping("")
    public String create(@LoginUser User loginUser, @Valid Question question) {
        questionService.create(loginUser, question);
        return "redirect:/";
    }

    @PutMapping("/{id:\\d+}")
    public String update(@LoginUser User loginUser, @PathVariable Long id, @Valid Question question) {
        questionService.update(loginUser, id, question);
        return "redirect:/questions/" + id;
    }

    @DeleteMapping("/{id:\\d+}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) {
        try {
            questionService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException e) {
            throw new UnAuthorizedException();
        }
        return "redirect:/";
    }

}
