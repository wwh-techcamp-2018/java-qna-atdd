package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Autowired
    QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @RequestBody Question question) {
        Question saveQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + saveQuestion.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Question show(@PathVariable Long id) {
        return qnaService.findById(id).get();
    }

    @PutMapping("{id}")
    public Question update(@LoginUser User user, @PathVariable Long id, @RequestBody Question updateQuestion) {
        return qnaService.update(user, id, updateQuestion);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/"));
        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }

    @PostMapping("{questionId}/answers")
    public Answer addAnswer(@LoginUser User loginUser, @PathVariable Long questionId, @RequestBody Answer answer){
        return qnaService.addAnswer(loginUser, questionId, answer.getContents());
    }


}
