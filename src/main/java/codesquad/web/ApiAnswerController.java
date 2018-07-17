package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.validate.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User user, @PathVariable Long questionId, @RequestBody Answer answer) {
        Answer returnAnswer = qnaService.addAnswer(user, questionId, answer.getContents());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + returnAnswer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{answerId}")
    public RestResponse delete(@LoginUser User loginUser, @PathVariable Long questionId, @PathVariable Long answerId) {
        qnaService.deleteAnswer(loginUser, answerId);
        return new RestResponse()
                .addAttribute("questionId", questionId)
                .addAttribute("answerId", answerId);
    }


}
