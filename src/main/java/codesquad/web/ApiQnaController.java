package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.ResourceNotFoundException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQnaController {

    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<?> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question created = qnaService.create(loginUser, question);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + created.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question updatedQuestion) {
        return qnaService.update(loginUser, id, updatedQuestion);
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping("/{id}")
    public void delete(@LoginUser User user, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(user, id);
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity<?> createAnswer(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Answer answer) {
        Answer created = qnaService.addAnswer(loginUser, id, answer.getContents());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + created.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{questionId}/answers/{id}")
    public void deleteAnswer(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id) {
        qnaService.deleteAnswer(loginUser, id);
    }
}
