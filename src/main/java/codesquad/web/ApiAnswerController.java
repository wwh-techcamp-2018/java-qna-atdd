package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.exception.CannotDeleteException;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable Long questionId, String contents) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + savedAnswer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{answerId}")
    public Answer show(@PathVariable Long answerId) {
        return qnaService.findByAnswerId(answerId);
    }

    @DeleteMapping("/{answerId}")
    public Answer delete(@LoginUser User loginUser, @PathVariable Long answerId) throws CannotDeleteException {
        return qnaService.deleteAnswer(loginUser, answerId);
    }
}
