package codesquad.web;


import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Resource(name = "qnaService")
    QnaService qnaService;

    @PostMapping("")
    public String create(@LoginUser User loginUser , Question question){
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("{id}")
    public String show(@PathVariable Long id, Model model){
        Optional<Question> maybeQuestion = qnaService.findById(id);
        maybeQuestion.orElseThrow(UnAuthorizedException::new);
        model.addAttribute("question", qnaService.findById(id).get());
        return "qna/show";
    }

    @PutMapping("{id}")
    public String update(@PathVariable Long id, @LoginUser User user, Question updateQeustion){
        return "redirect:" + qnaService.update(user, id, updateQeustion).generateUrl();
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable Long id, @LoginUser User user) throws CannotDeleteException {
        qnaService.deleteQuestion(user,id);
        return "redirect:/";
    }

}
