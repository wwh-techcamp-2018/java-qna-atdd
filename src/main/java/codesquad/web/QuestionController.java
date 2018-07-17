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

// TODO 포비에게 물어보고 싶은 것
// 동작 방식에 대해 알아보고 싶을 때 어떻게 하시는지? 분석하는 과정에 대한 팁?
// repository 를 동시에 여러 사용자가 한 데이터에 접근하면 스프링이 막나? 데이터베이스가 막나?


@Controller
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        model.addAttribute("question", qnaService.findById(id)
                .filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new));
        return "/qna/updateForm";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).orElseThrow(IllegalArgumentException::new));
        return "/qna/show";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable Long id, Question updateQuestion) {
        qnaService.update(loginUser, id, updateQuestion);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
