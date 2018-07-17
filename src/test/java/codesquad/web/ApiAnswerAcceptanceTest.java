package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    public static final User other = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private User questionWriter;
    private Question question;

    @Before
    public void setUp() throws Exception {
        questionWriter = defaultUser();

        String location = createResource("/api/questions", new Question("(title)", "(contents)"), basicAuthTemplate(questionWriter), Void.class);
        question = getResource(location, Question.class, questionWriter);
    }

    @Test
    public void create() throws Exception {
        User answerWriter = questionWriter;
        String path = String.format("/api/questions/%d/answer", question.getId());

        Answer createdAnswer = createResource(path, new Answer("(contents)"), basicAuthTemplate(answerWriter));
        log.debug("response : {}", createdAnswer);
        assertThat(createdAnswer).isNotNull();
    }

    @Test
    public void create_로그인안함() throws Exception {
        String path = String.format("/api/questions/%d/answer", question.getId());

        ResponseEntity<Answer> response = template().postForEntity(path, new Answer("(contents)"), Answer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() throws Exception {
        User createUser = questionWriter;
        User deleteUser = createUser;
        delete(createUser, deleteUser, HttpStatus.OK);
    }

    @Test
    public void delete_다른사용자() throws Exception {
        User createUser = questionWriter;
        User deleteUser = other;
        delete(createUser, deleteUser, HttpStatus.FORBIDDEN);
    }

    private void delete(User createUser, User deleteUser, HttpStatus expectedHttpStatus) {
        User answerWriter = createUser;
        String createAnswerPath = String.format("/api/questions/%d/answer", question.getId());
        Answer createdAnswer = createResource(createAnswerPath, new Answer("(contents)"), basicAuthTemplate(answerWriter));

        String deleteAnswerPath = String.format("/api/questions/%d/answer/%d", question.getId(), createdAnswer.getId());

        ResponseEntity<Void> deleteResponse = basicAuthTemplate(deleteUser)
                .exchange(deleteAnswerPath, HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(expectedHttpStatus);
    }

    private Answer createResource(String path, Object bodyPayload, TestRestTemplate template) {
        ResponseEntity<Answer> response = template.postForEntity(path, bodyPayload, Answer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }
}
