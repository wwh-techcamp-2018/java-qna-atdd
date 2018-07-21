package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private HtmlFormDataBuilder htmlFormDataBuilder;
    private HttpHeaders jsonHeader;

    @Before
    public void formBuilderSet() {
        htmlFormDataBuilder = HtmlFormDataBuilder.jsonForm();
    }

    @Test
    public void addAnswer_성공() {
        Question question = defaultQuestion();
        Answer answer = new Answer(null, "TestAnswer");
        log.debug("request body : {}", htmlFormDataBuilder);
        ResponseEntity<Answer> response = basicAuthTemplate(defaultUser()).postForEntity(String.format("/api/questions/%d/answers/", question.getId()), createHttpEntity(answer), Answer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getQuestion()).isNotNull();
    }

    @Test
    public void addAnswer_실패() {

        Question question = defaultQuestion();
        Answer answer = new Answer(null, "TestAnswer");
        ResponseEntity<String> responseUnathorized = template().postForEntity(String.format("/api/questions/%d/answers/", question.getId()), createHttpEntity(answer), String.class);
        ResponseEntity<String> responseNotFound = basicAuthTemplate(defaultUser()).postForEntity(String.format("/api/questions/%d/answers/", Long.MAX_VALUE - 1), createHttpEntity(answer), String.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(responseUnathorized.getStatusCode()).as("FORBIDDEN").isEqualTo(HttpStatus.FORBIDDEN);
        softAssertions.assertThat(responseNotFound.getStatusCode()).as("NOT_FOUND").isEqualTo(HttpStatus.NOT_FOUND);
        softAssertions.assertAll();
    }

    @Test
    public void delete_성공() {
        Question question = defaultQuestion();
        Answer answer = defaultAnswer();
        ResponseEntity<Answer> responseEntity = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d/answers/%d", question.getId(), answer.getId()), HttpMethod.DELETE, createHttpEntity(null), Answer.class);
        assertThat(responseEntity.getBody()).isEqualTo(answer);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_실패() {
        Question question = defaultQuestion();
        ResponseEntity<String> responseForbidden = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d/answers/%d", question.getId(), 2), HttpMethod.DELETE, createHttpEntity(null), String.class);
        ResponseEntity<String> responseNotFound = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d/answers/%d", 2, 3), HttpMethod.DELETE, createHttpEntity(null), String.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(responseForbidden.getStatusCode()).as("FORBIDDEN").isEqualTo(HttpStatus.FORBIDDEN);
        softAssertions.assertThat(responseNotFound.getStatusCode()).as("NOT_FOUND").isEqualTo(HttpStatus.NOT_FOUND);
        softAssertions.assertAll();
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}

