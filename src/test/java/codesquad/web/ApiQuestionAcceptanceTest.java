package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionTest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.JAVAJIGI;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    private Question question;
    @Before
    public void setUp() throws Exception {
        question = QuestionTest.newQuestion("test title", "test contents");
    }

    @Test
    public void createTest() {
        ResponseEntity<Question> response = basicAuthTemplate().postForEntity(String.format("/api/questions"), question, Question.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getContents()).isEqualTo("test contents");
        assertThat(response.getHeaders().getLocation().getPath().startsWith(String.format("/api/questions")))
                .isTrue();
    }

    @Test
    public void updateTest() {
        question.writeBy(JAVAJIGI);
        ResponseEntity<Question> response = basicAuthTemplate().postForEntity(String.format("/api/questions"), question, Question.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Question originalQuestion = response.getBody();

        Question updateQuestion = QuestionTest.newQuestion("update title", "update contents");


        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d",originalQuestion.getId())
                        , HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getContents()).isEqualTo(updateQuestion.getContents());
    }


    @Test
    public void deleteTest() {
        question.writeBy(JAVAJIGI);
        ResponseEntity<Question> response = basicAuthTemplate().postForEntity(String.format("/api/questions"), question, Question.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Question originalQuestion = response.getBody();


        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d",originalQuestion.getId())
                        , HttpMethod.DELETE, createHttpEntity(null), Question.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
