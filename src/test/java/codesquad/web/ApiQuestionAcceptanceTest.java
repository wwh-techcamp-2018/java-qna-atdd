package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.validate.RestResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    private ResponseEntity requestQuestionCreation() {
        return requestQuestionCreation(null);
    }

    private ResponseEntity requestQuestionCreation(Question question) {
        if (Objects.isNull(question))
            question = new Question("title_good", "contents_long");

        ResponseEntity<Void> response = basicAuthTemplate(defaultUser())
                .postForEntity(
                        "/api/questions",
                        question,
                        Void.class
                );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }

    @Test
    public void create() {
        requestQuestionCreation();
    }

    @Test
    public void list() {
        ResponseEntity<Question[]> response = template().getForEntity("/api/questions", Question[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(questionRepository.findByDeleted(false)).containsExactlyInAnyOrder(response.getBody());
    }

    @Test
    public void show() {
        Question question = new Question("title_longlong", "contents_long long");
        ResponseEntity responseEntity = requestQuestionCreation(question);
        String location = responseEntity.getHeaders().getLocation().getPath();

        ResponseEntity<Question> questionResponse = template().getForEntity(location, Question.class);
        assertThat(questionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(questionResponse.getBody().getTitle()).isEqualTo(question.getTitle());
        assertThat(questionResponse.getBody().getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void update() throws Exception {
        ResponseEntity responseEntity = requestQuestionCreation();
        String location = responseEntity.getHeaders().getLocation().getPath();

        Question updateQuestion = new Question("title_updated", "contents_updated");
        ResponseEntity<Question> questionResponse = basicAuthTemplate(defaultUser())
                .exchange(
                        location,
                        HttpMethod.PUT,
                        createHttpEntity(updateQuestion),
                        Question.class
                );
        assertThat(questionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Question responseQuestion = questionResponse.getBody();
        Question dbQuestion = questionRepository.findByIdAndDeletedFalse(responseQuestion.getId())
                .orElseThrow(Exception::new);

        assertThat(responseQuestion.getTitle()).isEqualTo(dbQuestion.getTitle());
        assertThat(responseQuestion.getContents()).isEqualTo(dbQuestion.getContents());
    }

    @Test
    public void delete() {
        ResponseEntity responseEntity = requestQuestionCreation();
        String location = responseEntity.getHeaders().getLocation().getPath();

        ResponseEntity<RestResponse> restResponse = basicAuthTemplate(defaultUser())
                .exchange(
                        location,
                        HttpMethod.DELETE,
                        createHttpEntity(),
                        RestResponse.class
                );
        assertThat(restResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestResponse r = restResponse.getBody();
        assertThat(r.isStatus()).isEqualTo(true);
    }

    @Test
    public void deleteFail_다른사람() {
        ResponseEntity responseEntity = requestQuestionCreation();
        String location = responseEntity.getHeaders().getLocation().getPath();

        ResponseEntity<RestResponse> restResponse = basicAuthTemplate(findByUserId("yeon"))
                .exchange(
                        location,
                        HttpMethod.DELETE,
                        createHttpEntity(),
                        RestResponse.class
                );
        assertThat(restResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteFail_글이없을때() {
        ResponseEntity<RestResponse> restResponse = basicAuthTemplate(defaultUser())
                .exchange(
                        "/api/questions/200",
                        HttpMethod.DELETE,
                        createHttpEntity(),
                        RestResponse.class
                );
        assertThat(restResponse.getStatusCode()).isEqualTo(HttpStatus.GONE);
        RestResponse r = restResponse.getBody();
        assertThat(r.isStatus()).isEqualTo(false);
    }
}
