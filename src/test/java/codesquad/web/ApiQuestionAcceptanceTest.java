package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private Question question;

    @Resource
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        question = new Question("종완님 어디사세요?", "장한평이요");
    }

    @Test
    public void create() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Question savedQuestion = basicAuthTemplate().getForObject(location, Question.class);
        assertThat(savedQuestion).isNotNull();
        assertThat(savedQuestion.getWriter()).isEqualTo(defaultUser());
    }

    @Test
    public void update() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Question savedQuestion = basicAuthTemplate().getForObject(location, Question.class);

        savedQuestion.setTitle("수정본 제목");
        savedQuestion.setContents("수정본 내용");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(savedQuestion), Question.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getTitle()).isEqualTo(savedQuestion.getTitle());
    }

    @Test
    public void delete() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Question savedQuestion = basicAuthTemplate().getForObject(location, Question.class);

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, new HttpEntity(new HttpHeaders()), Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(questionRepository.findById(savedQuestion.getId())
                .orElseThrow(IllegalArgumentException::new)
                .isDeleted())
                .isTrue();
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
