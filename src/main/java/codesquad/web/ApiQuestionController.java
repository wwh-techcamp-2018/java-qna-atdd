package codesquad.web;

import codesquad.domain.Question;
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

@RequestMapping("/api/questions")
@RestController
public class ApiQuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question newQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + newQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{questionId}")
    public Question get(@PathVariable Long questionId) {
        return qnaService.findById(questionId).get();
    }

    @PutMapping("/{questionId}")
    public Question update(@LoginUser User loginUser, @PathVariable Long questionId, @Valid @RequestBody Question question) {
        return qnaService.update(loginUser, questionId, question);
    }

    @DeleteMapping("/{questionId}")
    public Question delete(@LoginUser User loginUser, @PathVariable Long questionId) {
        return qnaService.deleteQuestion(loginUser, questionId);
    }
}
