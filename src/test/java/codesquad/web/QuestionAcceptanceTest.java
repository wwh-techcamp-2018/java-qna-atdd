package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.builder.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void createForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void create() throws Exception {
        String ramdomTitle = UUID.randomUUID().toString();
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("title", ramdomTitle)
                .addParameter("contents", "test_contents")
                .build();

        assertThat(questionRepository.findByTitle(ramdomTitle)).isEmpty();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");

        assertThat(questionRepository.findByTitle(ramdomTitle)).hasSize(1);
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        log.debug("body : {}", body);
        List<Question> questions = questionRepository.findByDeleted(false);
        questions.stream().forEach(question -> {
            assertThat(body).contains(question.getTitle());
            assertThat(body).contains(question.getWriter().getName());
        });
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/1/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateFormAsWriter() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/1/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateFormNotWriter() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/2/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template(), 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> update(TestRestTemplate template, Long questionId) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.put()
                .addParameter("title", "update_title")
                .addParameter("contents", "update_contents")
                .build();

        return template.postForEntity(String.format("/questions/%d", questionId), request, String.class);
    }

    @Test
    public void updateAsWriter() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate(), 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void updateAsNotWriter() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate(), 2L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_no_login() throws Exception {
        ResponseEntity<String> response = delete(template(), 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteAsWriter() throws Exception {
        ResponseEntity<String> response = delete(basicAuthTemplate(), 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void deleteAsNotWriter() throws Exception {
        ResponseEntity<String> response = delete(basicAuthTemplate(), 2L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> delete(TestRestTemplate template, Long questionId) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.delete().build();
        return template.postForEntity(String.format("/questions/%d", questionId), request, String.class);
    }

    @Test
    public void show() {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        Question question = questionRepository.findById(1L).get();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody()).contains(question.getTitle());
    }

}
