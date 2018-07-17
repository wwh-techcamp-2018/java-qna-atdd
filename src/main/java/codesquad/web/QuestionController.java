package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/qnas")
@Controller
public class QuestionController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping
    public String list(Model model) {
        List<Question> qnas = (List<Question>) qnaService.findAll();
        model.addAttribute("questions", qnas);
        return "/qna/list";
    }

    @GetMapping("/{questionId}")
    public String show(@PathVariable Long questionId, Model model) {
        Question question = qnaService.findById(questionId).get();
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/qnas";
    }

    @GetMapping("/form/{questionId}")
    public String updateForm(Model model, @LoginUser User loginUser, @PathVariable Long questionId) {
        Question question = qnaService.findById(questionId).get();
        model.addAttribute("questionId", questionId)
            .addAttribute("title", question.getTitle())
            .addAttribute("contents", question.getContents());
        return "/qna/updateForm";
    }

    @PutMapping("/{questionId}")
    public String update(@PathVariable Long questionId, @LoginUser User loginUser, Question newQuestion) {
        qnaService.update(loginUser, questionId, newQuestion);
        return "redirect:/qnas";
    }

    @DeleteMapping("/{questionId}")
    public String delete(@PathVariable Long questionId, @LoginUser User loginUser) {
        qnaService.deleteQuestion(loginUser, questionId);
        return "redirect:/qnas";
    }



}
