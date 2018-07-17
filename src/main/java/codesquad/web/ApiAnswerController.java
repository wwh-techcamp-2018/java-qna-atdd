package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("/{questionId}/answer")
    public ResponseEntity<Answer> create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody String content) {
        Answer createdAnswer = qnaService.addAnswer(loginUser, questionId, content);
        return new ResponseEntity<Answer>(createdAnswer, new HttpHeaders(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{questionId}/answer/{answerId}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long answerId) {
        HttpHeaders headers = new HttpHeaders();
        try {
            qnaService.deleteAnswer(loginUser, answerId);
            headers.setLocation(URI.create(String.format("/api/questions/%d", questionId)));
            return new ResponseEntity<Void>(headers, HttpStatus.OK);
        } catch (CannotDeleteException e) {
            return new ResponseEntity<Void>(headers, HttpStatus.FORBIDDEN);
        }
    }
}
