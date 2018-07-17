package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.exception.CannotDeleteException;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Question> create(@LoginUser User loginUser, @RequestBody Question question) {
        Question createQuestion = qnaService.create(loginUser, question);
        return ResponseEntity.created(URI.create(
                String.format("/api/questions/%d", createQuestion.getId())))
                .body(createQuestion);
    }

    @PutMapping("/{id}")
    public Question update(@LoginUser User loginUser, @RequestBody Question question, @PathVariable Long id) {
        return qnaService.update(loginUser, id, question);
    }

    @DeleteMapping("/{id}")
    public Question delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        return qnaService.deleteQuestion(loginUser,id);
    }


}
