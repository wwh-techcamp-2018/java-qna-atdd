package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import support.util.HtmlFormDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    private HtmlFormDataBuilder htmlFormDataBuilder;
    private Answer deletedAnswer;

    @Before
    public void setUp() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();

        deletedAnswer = new Answer(defaultUser(), "deleted");
        deletedAnswer.toQuestion(defaultQuestion());
        answerRepository.save(deletedAnswer);
    }

    @Test
    public void create() throws Exception {
        Answer newAnswer = new Answer(defaultUser(), "answer1");
        String location = createResource("/api" + defaultQuestion().generateUrl() + "/answers", createHttpEntity(newAnswer));
        assertThat(location).startsWith("/api" + defaultQuestion().generateUrl() + "/answers/");
    }

    @Test
    public void create_no_login() throws Exception {
        Answer newAnswer = new Answer(defaultUser(), "answer1");
        createResourceWithoutLogin("/api" + defaultQuestion().generateUrl() + "/answers", createHttpEntity(newAnswer));
    }

    @Test
    public void delete() {
        delete(basicAuthTemplate(defaultUser()), deletedAnswer, HttpStatus.OK);
    }

    @Test
    public void delete_no_login() {
        delete(template(), deletedAnswer, HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_not_owner() {
        delete(basicAuthTemplate(otherUser()), deletedAnswer, HttpStatus.FORBIDDEN);
    }

    private void delete(TestRestTemplate template, Answer answer, HttpStatus status) {
        ResponseEntity<Void> response = template.exchange("/api" + answer.generateUrl(), HttpMethod.DELETE, htmlFormDataBuilder.build(), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(status);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
