package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);


    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Answer> create(@LoginUser User loginUser, @PathVariable Long questionId, @RequestBody Answer answer) {
        Answer createAnswer = qnaService.addAnswer(loginUser, questionId, answer.getContents());
        return ResponseEntity.created(URI.create(
                String.format("/api/questions/%d/answers/%d", createAnswer.getQuestion().getId(), createAnswer.getId()))).
                body(createAnswer);
    }

    @DeleteMapping("/{answerId}")
    public Answer delete(@LoginUser User loginUser, @PathVariable Long answerId) {
        Answer deleteAnswer = qnaService.deleteAnswer(loginUser, answerId);

        return deleteAnswer;
    }


}
