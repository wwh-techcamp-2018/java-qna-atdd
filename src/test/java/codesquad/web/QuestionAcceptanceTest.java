package codesquad.web;

import codesquad.domain.AnswerRepository;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                        .urlEncodedForm()
                        .addParameter("title", "title")
                        .addParameter("contents", "contents")
                        .build();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void update() {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                        .urlEncodedForm()
                        .addParameter("_method", "put")
                        .addParameter("title", "updatedTitle")
                        .addParameter("contents", "updatedContents")
                        .build();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions/1", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(questionRepository.findById(1L).get().getTitle()).isEqualTo("updatedTitle");
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void delete() {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                        .urlEncodedForm()
                        .addParameter("_method", "delete")
                        .build();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions/1", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(questionRepository.findById(1L).get().isDeleted()).isEqualTo(true);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void addAnswer() {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                        .urlEncodedForm()
                        .addParameter("contents", "testestestest")
                        .build();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions/1/answers", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/1");
    }

    @Test
    public void deleteAnswer() {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                        .urlEncodedForm()
                        .addParameter("_method", "delete")
                        .build();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions/1/answers/1", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/1");
    }

    @Test
    public void showDetail() {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }
}
