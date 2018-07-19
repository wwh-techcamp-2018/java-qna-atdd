package codesquad.web;

import codesquad.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    private Question createdQuestion;

    private Question updatedQuestion;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdQuestion = new Question("Test Title", "Test contents");
        updatedQuestion = new Question("update Title", "update contents");
    }

    @Test
    public void create() throws Exception {
        String location = createResource("/api/questions", createdQuestion, UserTest.JAVAJIGI);
        Question dbQuestion = basicAuthTemplate().getForObject(location, Question.class);
        assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void create_not_login() throws Exception {
        ResponseEntity<Void> response = template().postForEntity("/api/questions", createdQuestion, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void show() {
        String location = createResource("/api/questions", createdQuestion, defaultUser());

        Question question = getResource(location, Question.class, User.GUEST_USER);
        assertThat(question.getTitle()).isEqualTo(createdQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(createdQuestion.getContents());
    }

    @Test
    public void update_succeed() {
        String location = createResource("/api/questions", createdQuestion, defaultUser());

        ResponseEntity<Question> response = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntityWithBody(updatedQuestion), Question.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo(updatedQuestion.getTitle());
        assertThat(response.getBody().getContents()).isEqualTo(updatedQuestion.getContents());
    }

    @Test
    public void update_no_login() throws Exception {
        String location = createResource("/api/questions", createdQuestion, defaultUser());

        ResponseEntity<Question> response = basicAuthTemplate(User.GUEST_USER)
                .exchange(location, HttpMethod.PUT, createHttpEntityWithBody(updatedQuestion), Question.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_다른_사람() throws Exception {
        String location = createResource("/api/questions", createdQuestion, defaultUser());

        ResponseEntity<Question> response = basicAuthTemplate(UserTest.SANJIGI)
                .exchange(location, HttpMethod.PUT, createHttpEntityWithBody(updatedQuestion), Question.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_succeed() {
        String location = createResource("/api/questions", createdQuestion, defaultUser());

        ResponseEntity<Question> response = basicAuthTemplate()
                .exchange(location, HttpMethod.DELETE, createHttpEntity(), Question.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Question question = getResource(location, Question.class, defaultUser());
        assertThat(question).isNull();
    }

    @Test
    public void delete_not_writer() {
        String location = createResource("/api/questions", createdQuestion, defaultUser());

        ResponseEntity<Question> response = basicAuthTemplate(UserTest.SANJIGI)
                .exchange(location, HttpMethod.DELETE, createHttpEntity(), Question.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_no_login() {
        String location = createResource("/api/questions", createdQuestion, defaultUser());

        ResponseEntity<Question> response = basicAuthTemplate(User.GUEST_USER)
                .exchange(location, HttpMethod.DELETE, createHttpEntity(), Question.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
