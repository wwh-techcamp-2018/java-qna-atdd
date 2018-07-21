package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
     private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);
    @Resource(name = "qnaService")
    private QnaService qnaService;


    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Answer create(@LoginUser User loginUser, @Valid @RequestBody Answer newAnswer, @PathVariable long questionId) {
        log.debug("JSON new answer : {}", newAnswer);
        return qnaService.addAnswer(loginUser, questionId, newAnswer.getContents());
    }

    @DeleteMapping("/{answerId}")
    @ResponseStatus(HttpStatus.OK)
    public Answer delete(@LoginUser User loginUser, @PathVariable long answerId){
        log.debug("delete method");
        return qnaService.deleteAnswer(loginUser, answerId);
    }



}
