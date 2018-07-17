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
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;


    @GetMapping("/form")
    public String createForm() {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("{id}")
    public String show(@PathVariable Long id, Model model) {
        Question question = qnaService.findById(id).get();
        model.addAttribute("question", question);

        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        Question question = qnaService.findByIdAndUser(loginUser, id);
        model.addAttribute("question",question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser,@PathVariable Long id, Question questionTarget) {
        return "redirect:/questions/"+qnaService.update(loginUser,id,questionTarget).getId();
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser,id);
        return "redirect:/";
    }

}
