package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.ApiAcceptanceTest;

import static codesquad.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends ApiAcceptanceTest {
    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void create_guest() {
        User guest = newUser("포비");
        Answer answer = new Answer(guest, "크롱사랑");
        ResponseEntity<Void> response = template().postForEntity("/api/questions/1/answers", answer, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_login_user() {
        Answer answer = new Answer(defaultUser(), "크롱사랑<3");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/1/answers", answer, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(answerRepository.findById(3L).isPresent()).isTrue();
    }

    @Test
    public void delete_guest() {
        User guest = newUser("포비");
        ResponseEntity<Void> response = template().exchange("/api/questions/1/answers/1", HttpMethod.DELETE, createHttpEntity(), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_owner() {
        ResponseEntity<Void> response = basicAuthTemplate(newUser("sanjigi", "test")).exchange("/api/questions/1/answers/1", HttpMethod.DELETE, createHttpEntity(null), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_owner() {
        ResponseEntity<Void> response = basicAuthTemplate().exchange("/api/questions/1/answers/1", HttpMethod.DELETE, createHttpEntity(), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(answerRepository.findById(1L).get().isDeleted()).isTrue();
    }
}
