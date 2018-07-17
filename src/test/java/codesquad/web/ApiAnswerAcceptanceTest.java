package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.QuestionSetUp;
import support.test.AcceptanceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    private List<Question> questions;
    private Question question;
    private Answer answer;

    @Before
    public void setUp() {
        answerRepository.deleteAll();
        questions = QuestionSetUp.setUp(questionRepository, defaultUser());
        question = questions.get(0);
        answer = getResource(createResourceWithDefaultUser(generateApiUrl(question), "댓글댓글댓글"), Answer.class, defaultUser());
    }

    @Test
    public void create() {
        assertThat(answer.getWriter()).isEqualTo(defaultUser());
        assertThat(answer.getContents()).isEqualTo("댓글댓글댓글");
    }

    @Test
    public void create_no_login() {
        ResponseEntity<String> response = template().postForEntity(generateApiUrl(question), "댓글댓글댓글", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(
                generateApiUrl(question) + "/" + answer.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(answerRepository.findById(answer.getId()).get().isDeleted()).isTrue();
    }


    @Test
    public void delete_no_login() {
        ResponseEntity<Void> responseEntity = template().exchange(
                generateApiUrl(question) + "/" + answer.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isFalse();
    }

    private String generateApiUrl(Question question) {
        return String.format("/api/questions/%d/answers", question.getId());
    }
}
