package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
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
        ResponseEntity<Void> response = createResource("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        Question savedQuestion = getResourceByDefaultUser(location, Question.class);

        assertThat(savedQuestion).isNotNull();
        assertThat(savedQuestion.getWriter()).isEqualTo(defaultUser());
    }

    @Test
    public void update() {
        ResponseEntity<Void> response = createResource("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        Question savedQuestion = getResourceByDefaultUser(location, Question.class);

        savedQuestion.setTitle("수정본 제목");
        savedQuestion.setContents("수정본 내용");

        ResponseEntity<Question> responseEntity = updateResourceByDefaultUser(location, createHttpEntity(savedQuestion), Question.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getTitle()).isEqualTo(savedQuestion.getTitle());
    }

    @Test
    public void delete_작성자와_지우려는자_불일치() {
        ResponseEntity<Void> response = createResource("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Question savedQuestion = getResourceByDefaultUser(location, Question.class);


        ResponseEntity<Void> responseEntity =
                deleteResourceBySpecificUser(findByUserId("gusdk"), location, new HttpEntity(new HttpHeaders()), Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(questionRepository.findById(savedQuestion.getId())
                .orElseThrow(IllegalArgumentException::new)
                .isDeleted())
                .isFalse();
    }

    @Test
    public void delete_답변없는_질문() {
        ResponseEntity<Void> response = createResource("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Question savedQuestion = getResourceByDefaultUser(location, Question.class);


        ResponseEntity<Void> responseEntity =
                deleteResourceByDefaultUser(location, new HttpEntity(new HttpHeaders()), Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(questionRepository.findById(savedQuestion.getId())
                .orElseThrow(IllegalArgumentException::new)
                .isDeleted())
                .isTrue();
    }

    @Test
    public void delete_내_답변만_있는_질문() {
        ResponseEntity<Void> response = createResource("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Question savedQuestion = getResourceByDefaultUser(location, Question.class);


        String contents = "홍종완씨입니다";
        ResponseEntity<Answer> answerResponse = createResource(
                String.format("/api/questions/%d/answers", savedQuestion.getId())
                , contents
                , Answer.class);
        Answer savedAnswer = answerResponse.getBody();
        assertThat(savedAnswer.getContents()).isEqualTo(contents);


        ResponseEntity<Void> responseEntity =
                deleteResourceByDefaultUser(location, new HttpEntity(new HttpHeaders()), Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(questionRepository.findById(savedQuestion.getId())
                .orElseThrow(IllegalArgumentException::new)
                .isDeleted())
                .isTrue();


    }

    @Test
    public void delete_타인의_답변도_있는_질문() {
        ResponseEntity<Void> response = createResource("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Question savedQuestion = getResourceByDefaultUser(location, Question.class);

        String contents = "홍종완씨입니다";
        ResponseEntity<Answer> answerResponse = createResourceBySpecificUser(
                String.format("/api/questions/%d/answers", savedQuestion.getId())
                , contents
                , Answer.class
                , findByUserId("gusdk"));
        Answer savedAnswer = answerResponse.getBody();
        assertThat(savedAnswer.getContents()).isEqualTo(contents);

        ResponseEntity<Void> responseEntity =
                deleteResourceByDefaultUser(
                        String.format("/api/questions/%d/answers/%d", savedQuestion.getId(), savedAnswer.getId())
                        , new HttpEntity(new HttpHeaders())
                        , Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(questionRepository.findById(savedQuestion.getId())
                .orElseThrow(IllegalArgumentException::new)
                .isDeleted())
                .isFalse();
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
