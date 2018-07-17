package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question createdQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + createdQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{questionId}")
    public Question show(@PathVariable long questionId) {
        return qnaService.findById(questionId)
                        .orElseThrow(EntityNotFoundException::new);
    }

    @PutMapping("/{questionId}")
    public Question update(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody Question target) {
        return qnaService.update(loginUser, questionId, target);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long questionId) {
        HttpHeaders headers = new HttpHeaders();
        try {
            qnaService.deleteQuestion(loginUser, questionId);
            headers.setLocation(URI.create("redirect:/"));
            return new ResponseEntity<Void>(headers, HttpStatus.OK);
        } catch (CannotDeleteException e) {
            return new ResponseEntity<Void>(headers, HttpStatus.FORBIDDEN);
        }
    }
}
