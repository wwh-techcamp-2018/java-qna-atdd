package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.UserTest;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    public static final Question DEFAULT_QUESTION = new Question("title", "contentscontents");
    public static final Question UPDATED_QUESTION = new Question("updatedTitle", "updatedContentscontents");

    @Test
    public void create_성공() {
        String location = createResourceForBasicAuth(defaultUser(), "/api/questions", DEFAULT_QUESTION);

        Question dbQuestion = getResource(location, Question.class, defaultUser());
        assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void create_실패() {
        ResponseEntity<Question> response =
            template().postForEntity("/api/questions", createHttpEntity(DEFAULT_QUESTION), Question.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void update_성공() {
        String location = createResourceForBasicAuth(defaultUser(), "/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Question> response =
                basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Question.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(UPDATED_QUESTION.isEqualsTitleAndContents(response.getBody())).isTrue();
    }

    @Test
    public void update_다른_사람() {
        String location = createResourceForBasicAuth(defaultUser(), "/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Question> response =
                basicAuthTemplate(UserTest.SANJIGI).exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Question.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_성공() {
        String location = createResourceForBasicAuth(defaultUser(), "/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Question> response =
                basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.DELETE, createEmptyHttpEntity(), Question.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isDeleted()).isTrue();
    }

    @Test
    public void delete_다른_사람() {
        String location = createResourceForBasicAuth(defaultUser(), "/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Question> response =
                basicAuthTemplate(UserTest.SANJIGI).exchange(location, HttpMethod.DELETE, createEmptyHttpEntity(), Question.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
