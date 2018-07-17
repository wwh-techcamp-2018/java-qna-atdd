package codesquad.web;

import codesquad.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import support.QuestionSetUp;
import support.StringUtil;
import support.test.AcceptanceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    private static final String QUESTION_API = "/api/questions";

    private List<Question> questions;
    private Question question;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        questions = QuestionSetUp.setUp(questionRepository, defaultUser());
        question = questions.get(0);
    }

    @Test
    public void create() {
        Question question = QuestionTest.newQuestion("Test_title_1");
        question.writeBy(defaultUser());

        String location = createResourceWithDefaultUser(QUESTION_API, question);

        Question responseQuestion= basicAuthTemplate(defaultUser()).getForObject(location, Question.class);
        assertThat(responseQuestion.getTitle()).isEqualTo(question.getTitle());
        assertThat(responseQuestion.getContents()).isEqualTo(question.getContents());
        assertThat(responseQuestion.getWriter()).isEqualTo(question.getWriter());
    }

    @Test
    public void create_no_login() {
        Question question = QuestionTest.newQuestion("Test_title_1");
        ResponseEntity<Void> response = template().postForEntity(
                QUESTION_API,
                question,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void showList() {
        ResponseEntity<List<Question>> response =
                template().exchange(
                        QUESTION_API,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Question>>() {}
                        );

        assertThat(response.getBody())
                .containsAll(questions)
                .hasSize(questions.size());
    }

    @Test
    public void update() {
        Question updateQuestionData = QuestionTest.newQuestion(question.getId(),
                StringUtil.getUpdatedString(question.getTitle()),
                StringUtil.getUpdatedString(question.getContents())
        );

        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(
                generateApiUrl(question),
                HttpMethod.PUT,
                createHttpEntity(updateQuestionData),
                Void.class
        );

        Question updatedQuestion = questionRepository.findById(question.getId()).get();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedQuestion.getTitle()).isEqualTo(updateQuestionData.getTitle());
        assertThat(updatedQuestion.getContents()).isEqualTo(updateQuestionData.getContents());
    }

    @Test
    public void update_no_login() {
        Question updateQuestionData = QuestionTest.newQuestion(question.getId(),
                StringUtil.getUpdatedString(question.getTitle()),
                StringUtil.getUpdatedString(question.getContents())
        );

        ResponseEntity<Void> responseEntity = template().exchange(
                generateApiUrl(question),
                HttpMethod.PUT,
                createHttpEntity(updateQuestionData),
                Void.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(
                generateApiUrl(question),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isTrue();
    }


    @Test
    public void delete_no_login() {
        ResponseEntity<Void> responseEntity = template().exchange(
                generateApiUrl(question),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isFalse();
    }

    private String generateApiUrl(Question question) {
        return "/api" + question.generateUrl();
    }

}
