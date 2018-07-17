package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    @Test
    public void create() {
        Question question = new Question("title1", "contents1");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        Question dbQuestion = basicAuthTemplate().getForObject(location, Question.class);
        assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void update() {
        Question question = new Question("title1_updated", "contents1_updated");
        ResponseEntity<Question> response =
                basicAuthTemplate().exchange("/api/questions/1", HttpMethod.PUT, createHttpEntity(question), Question.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("title1_updated");
    }

    @Test
    public void update_no_login() {
        Question question = new Question("title1_updated", "contents1_updated");
        ResponseEntity<Void> response =
                template().exchange("/api/questions/1", HttpMethod.PUT, createHttpEntity(question), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_not_match() {
        Question question = new Question("title1_updated", "contents1_updated");
        User wrongUser = new User("sanjigi", "1", "", "");
        ResponseEntity<Void> response =
                basicAuthTemplate(wrongUser).exchange("/api/questions/1", HttpMethod.PUT, createHttpEntity(question), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() {
        ResponseEntity<Question> response =
                basicAuthTemplate().exchange("/api/questions/2", HttpMethod.DELETE, createHttpEntity(null), Question.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isDeleted()).isEqualTo(true);
    }

    @Test
    public void delete_no_login() {
        ResponseEntity<Void> response =
                template().exchange("/api/questions/1", HttpMethod.DELETE, createHttpEntity(null), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_not_match() {
        User wrongUser = new User("sanjigi", "1", "", "");
        ResponseEntity<Void> response =
                basicAuthTemplate(wrongUser).exchange("/api/questions/1", HttpMethod.DELETE, createHttpEntity(null), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
