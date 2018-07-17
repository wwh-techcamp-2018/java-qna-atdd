package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    @Resource
    private QnaService qnaService;

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @PostMapping("")
    public Answer create(@LoginUser User loginUser, @PathVariable Long questionId, @RequestBody String contents) {
        return qnaService.addAnswer(loginUser, questionId, contents);
    }

    @DeleteMapping("/{id}")
    public Answer delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        return qnaService.deleteAnswer(loginUser, id);
    }
}
