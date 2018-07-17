package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.QuestionRepository;
import codesquad.domain.UserRepository;
import codesquad.validate.RestResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void addAnswer() throws Exception {
        createObject(
                "/api/questions/1/answers",
                new Answer(defaultUser(), "long long very long ")
        );
    }

    @Test
    public void deleteAnswer() {
        String location = createObject(
                "/api/questions/1/answers",
                new Answer(defaultUser(), "long long very long ")
        );
        ResponseEntity<RestResponse> restResponse = basicAuthTemplate()
                .exchange(
                        location,
                        HttpMethod.DELETE,
                        createHttpEntity(),
                        RestResponse.class
                );

        assertThat(restResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestResponse r = restResponse.getBody();
        assertThat(r.isStatus()).isEqualTo(true);
        assertThat(r.getResult().get("questionId")).isEqualTo(1);
    }

    @Test
    public void deleteAnswerFail() {
        String location = createObject(
                "/api/questions/1/answers",
                new Answer(defaultUser(), "long long very long ")
        );
        ResponseEntity<RestResponse> restResponse = basicAuthTemplate(userRepository.findByUserId("yeon").get())
                .exchange(
                        location,
                        HttpMethod.DELETE,
                        createHttpEntity(),
                        RestResponse.class
                );

        assertThat(restResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
