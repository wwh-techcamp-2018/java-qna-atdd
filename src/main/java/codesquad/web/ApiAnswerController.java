package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/questions/{qid}/answers")
public class ApiAnswerController {
    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Answer> create(@LoginUser User loginUser, @PathVariable Long qid, @Valid @RequestBody Answer answer) {
        return new ResponseEntity<Answer>(qnaService.addAnswer(loginUser, qid, answer.getContents()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Answer> delete(@LoginUser User loginUser, @PathVariable Long id) {
        // TODO: 2018. 7. 17. 답변이 없는 경우
        return new ResponseEntity<Answer>(qnaService.deleteAnswer(loginUser, id), HttpStatus.OK);
    }
}
