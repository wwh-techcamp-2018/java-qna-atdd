package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    @Test
    public void create() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/1", "칼퇴각 !", Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        assertThat(dbAnswer.getWriter().getName()).isEqualTo("자바지기");
        Question dbQuestion = basicAuthTemplate().getForObject("/api/questions/1", Question.class);
        assertThat(dbQuestion.getAnswers().contains(dbAnswer));
    }

    @Test
    public void create_no_login() {
        ResponseEntity<Void> response = template().postForEntity("/api/questions/1", "칼퇴각 !", Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() {
        ResponseEntity<Answer> response =
                basicAuthTemplate().exchange("/api/questions/1/1", HttpMethod.DELETE, createHttpEntity(null), Answer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isDeleted()).isTrue();
    }

    @Test
    public void delete_no_login() {
        ResponseEntity<Void> response =
                template().exchange("/api/questions/1/1", HttpMethod.DELETE, createHttpEntity(null), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_not_match () {
        User wrongUser = new User("sanjigi", "1", "", "");
        ResponseEntity<Void> response =
                basicAuthTemplate(wrongUser).exchange("/api/questions/1/1", HttpMethod.DELETE, createHttpEntity(null), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
