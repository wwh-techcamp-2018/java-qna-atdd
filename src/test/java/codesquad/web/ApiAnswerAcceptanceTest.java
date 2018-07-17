package codesquad.web;


import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    private Question question;
    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        question = new Question("종완님 어디사세요?", "장한평이요");
        question.writeBy(defaultUser());
        question = questionRepository.save(question);
    }

    @Test
    public void create() {
        String contents = "홍종완씨입니다";
        ResponseEntity<Answer> response = basicAuthTemplate()
                .postForEntity(String.format("/api/questions/%d/answers", question.getId()), contents, Answer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContents()).isEqualTo(contents);
    }

    @Test
    public void delete() {
        String contents = "홍종완씨입니다";
        ResponseEntity<Answer> response = basicAuthTemplate()
                .postForEntity(String.format("/api/questions/%d/answers", question.getId()), contents, Answer.class);
        Answer savedAnswer = response.getBody();

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate().exchange(String.format("/api/questions/%d/answers/%d", savedAnswer.getQuestion().getId(), savedAnswer.getId()), HttpMethod.DELETE, new HttpEntity(new HttpHeaders()), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
