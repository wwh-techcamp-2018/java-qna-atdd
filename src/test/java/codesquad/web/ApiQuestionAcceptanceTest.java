package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import support.util.HtmlFormDataBuilder;

import static codesquad.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    private HtmlFormDataBuilder htmlFormDataBuilder;


    @Before
    public void setUp() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void create() throws Exception {
        Question newQuestion = new Question("title1", "contents1");
        String location = createResource("/api/questions", createHttpEntity(newQuestion));
        assertThat(location).startsWith("/api/questions/");
    }

    @Test
    public void create_no_login() throws Exception {
        Question newQuestion = new Question("title1", "contents1");
        createResourceWithoutLogin("/api/questions", createHttpEntity(newQuestion));
    }

    @Test
    public void show() {
        Question question = getResource("/api" + defaultQuestion().generateUrl(), Question.class);

        assertThat(defaultQuestion().getTitle()).isEqualTo(question.getTitle());
        assertThat(defaultQuestion().getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void update() throws Exception {
        Question updatedQuestion = new Question("delete", "delete contents");
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(defaultUser()).exchange("/api" + defaultQuestion().generateUrl(), HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        Question responseBody = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedQuestion.getTitle()).isEqualTo(responseBody.getTitle());
        assertThat(updatedQuestion.getContents()).isEqualTo(responseBody.getContents());
    }

    @Test
    public void update_no_login() throws Exception {
        Question updatedQuestion = new Question("delete", "delete contents");
        ResponseEntity<Question> responseEntity =
                template().exchange("/api" + defaultQuestion().generateUrl(), HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_not_owner() throws Exception {
        Question updatedQuestion = new Question("delete", "delete contents");
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(otherUser()).exchange("/api" + defaultQuestion().generateUrl(), HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() {
        Question deletedQuestion = new Question("origin-title", "origin-contents");
        deletedQuestion.writeBy(defaultUser());
        questionRepository.save(deletedQuestion);

        delete(basicAuthTemplate(defaultUser()), deletedQuestion, HttpStatus.OK);
    }

    @Test
    public void delete_no_login() {
        delete(template(), defaultQuestion(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_not_owner() {
        delete(basicAuthTemplate(otherUser()), defaultQuestion(), HttpStatus.FORBIDDEN);
    }

    private void delete(TestRestTemplate template, Question question, HttpStatus status) {
        ResponseEntity<Void> response = template.exchange("/api" + question.generateUrl(), HttpMethod.DELETE, htmlFormDataBuilder.build(), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(status);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
