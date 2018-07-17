package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.UserTest;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    public static final Answer DEFAULT_ANSWER = new Answer(UserTest.JAVAJIGI, "contentscontents");

    @Test
    public void create_성공() {
        String location = createResourceForBasicAuth(defaultUser(), "/api/questions/1/answers", DEFAULT_ANSWER);

        Answer dbAnswer = getResource(location, Answer.class, defaultUser());
        assertThat(dbAnswer).isNotNull();
    }

    @Test
    public void create_실패() {
        ResponseEntity<Answer> response =
                template().postForEntity("/api/questions/1/answers", createHttpEntity(DEFAULT_ANSWER), Answer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void delete_성공() {
        String location = createResourceForBasicAuth(defaultUser(), "/api/questions/1/answers", DEFAULT_ANSWER);

        ResponseEntity<Answer> response =
                basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.DELETE, createEmptyHttpEntity(), Answer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isDeleted()).isTrue();
    }

    @Test
    public void delete_실패() {
        String location = createResourceForBasicAuth(defaultUser(), "/api/questions/1/answers", DEFAULT_ANSWER);

        ResponseEntity<Answer> response =
                basicAuthTemplate(UserTest.SANJIGI).exchange(location, HttpMethod.DELETE, createEmptyHttpEntity(), Answer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}