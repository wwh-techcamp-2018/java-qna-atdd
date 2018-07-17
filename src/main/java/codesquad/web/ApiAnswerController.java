package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}")
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable Long questionId, @LoginUser User loginUser, String contents) {
        System.out.println("get into create function");
        Answer answer = qnaService.addAnswer(loginUser, questionId, contents);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/" + answer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{answerId}")
    public Answer get(@PathVariable Long questionId, @PathVariable Long answerId) {
        Question question = qnaService.findById(questionId).get();
        return question.getAnswers().stream().filter(a -> a.getId() == answerId).findFirst().get();
    }

    @DeleteMapping("/{answerId}")
    public Answer delete(@LoginUser User loginUser, @PathVariable Long questionId, @PathVariable Long answerId) {
        return qnaService.deleteAnswer(loginUser, answerId);
    }
}
