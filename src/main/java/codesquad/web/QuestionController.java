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

@Controller
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QnaService qnaService;

    @GetMapping("")
    public String list() {
        return "redirect:/";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return list();
    }

    @GetMapping("/{qid}")
    public String show(@PathVariable Long qid, Model model) {
        // TODO: 2018. 7. 17. 존재하지 않는 질문에 대해서 예외처리가 필요합니다.
        model.addAttribute("question", qnaService.findById(qid).get());
        return "qna/show";
    }

    @PostMapping("/{qid}")
    public String update(@LoginUser User loginUser, @PathVariable Long qid, Question question) {
        qnaService.update(loginUser, qid, question);
        return "redirect:/questions/" + qid;
    }

    @GetMapping("/{qid}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long qid, Model model) {
        // TODO: 2018. 7. 17. 존재하지 않는 질문에 대해서 예외처리가 필요합니다.
        Question question = qnaService.findById(qid).get();
        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        model.addAttribute("question", question);
        return "qna/updateForm";
    }

    @DeleteMapping("/{qid}")
    public String delete(@LoginUser User loginUser, @PathVariable Long qid) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, qid);
        return list();
    }
}
