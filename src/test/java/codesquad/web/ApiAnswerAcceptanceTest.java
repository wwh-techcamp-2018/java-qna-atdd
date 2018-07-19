package codesquad.web;

import codesquad.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Autowired
    private AnswerRepository answerRepository;

    private Question createdQuestion;

    private Answer createdAnswer;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdQuestion = new Question("Test Title", "Test contents");
        createdAnswer = new Answer("answer_comment");
    }

    @Test
    public void create() throws Exception {
        String location = createResource("/api/questions", createdQuestion, UserTest.JAVAJIGI);
        createResource(location + "/answers", createdAnswer, UserTest.JAVAJIGI);
        Answer answer = basicAuthTemplate().getForObject(location, Answer.class);
        assertThat(answer).isNotNull();
    }

    @Test
    public void create_no_login() throws Exception {
        String location = createResource("/api/questions", createdQuestion, UserTest.JAVAJIGI);

        Answer createdAnswer = new Answer("answer_comment");
        ResponseEntity<Void> answerResponse = template().postForEntity(location + "/answers", createdAnswer, Void.class);
        assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_succeed() {
        String location = createResource("/api/questions", createdQuestion, UserTest.JAVAJIGI);
        String answerLocation = createResource(location + "/answers", createdAnswer, UserTest.JAVAJIGI);

        ResponseEntity<Answer> deletedResponse = basicAuthTemplate()
                .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(), Answer.class);
        assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void delete_not_writer() {
        String location = createResource("/api/questions", createdQuestion, UserTest.JAVAJIGI);
        String answerLocation = createResource(location + "/answers", createdAnswer, UserTest.JAVAJIGI);

        ResponseEntity<Answer> deletedResponse = basicAuthTemplate(UserTest.SANJIGI)
                .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(), Answer.class);
        assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_no_login() {
        String location = createResource("/api/questions", createdQuestion, UserTest.JAVAJIGI);
        String answerLocation = createResource(location + "/answers", createdAnswer, UserTest.JAVAJIGI);

        ResponseEntity<Answer> deletedResponse = basicAuthTemplate(User.GUEST_USER)
                .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(), Answer.class);
        assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
