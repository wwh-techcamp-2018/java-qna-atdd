package codesquad.web;

import codesquad.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    private static final Long QUESTION_ID= 1L;
    private static final Long ANSWER_ID = 1L;

    @Test
    public void create() {
        ResponseEntity<Answer> response = basicAuthTemplate().postForEntity(
                String.format("/api/questions/%d/answers",QUESTION_ID), AnswerTest.newAnswer("test answer"), Answer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getContents()).isEqualTo("test answer");
        assertThat(response.getHeaders().getLocation().getPath().startsWith(String.format("/api/questions/%d/answers",QUESTION_ID)))
                .isTrue();
    }

    @Test
    public void delete() {
        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d/answers/%d",QUESTION_ID,ANSWER_ID)
                        , HttpMethod.DELETE, createHttpEntity(null), Answer.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().isDeleted()).isTrue();

    }


    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
