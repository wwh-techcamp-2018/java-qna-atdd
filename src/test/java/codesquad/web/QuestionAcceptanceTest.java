package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        User loginUser = defaultUser();

        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        String title = UUID.randomUUID().toString();
        builder.addParameter("title", title);
        builder.addParameter("contents", "이혁진인데요");

        assertThat(questionRepository.findByTitle(title).size()).isEqualTo(0);

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", request, String.class);

        assertThat(questionRepository.findByTitle(title).size()).isEqualTo(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void show() {
        long questionId = getDefaultQuestionId();
        log.debug("question id : {}", questionId);
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", questionId), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody()).contains(defaultUser().getName());
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("response path: {}", response.getHeaders().getLocation().getPath());
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }


    @Test
    public void updateForm_no_login() throws Exception {
        long questionId = getDefaultQuestionId();
        log.debug("question id : {}", questionId);
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", questionId),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm_login() throws Exception {
        long questionId = getDefaultQuestionId();
        log.debug("question id : {}", questionId);
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .getForEntity(String.format("/questions/%d/form", questionId), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(EntityNotFoundException::new);
        assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void update() {
        User user = defaultUser();
        long questionId = getDefaultQuestionId();
        log.debug("question id : {}", questionId);

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "(updated title)")
                .addParameter("contents", "(updated contents)")
                .build();

        String url = String.format("/questions/%d", questionId);

        ResponseEntity<String> response = basicAuthTemplate(user)
                .postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("response path: {}", response.getHeaders().getLocation().getPath());
        assertThat(response.getHeaders().getLocation().getPath()).startsWith(url);
    }

    @Test
    public void delete() {
        User user = defaultUser();
        long questionId = getDefaultQuestionId();

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                                                            .delete()
                                                            .build();

        String url = String.format("/questions/%d", questionId);

        ResponseEntity<String> response = basicAuthTemplate(user)
                                    .postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("response path: {}", response.getHeaders().getLocation().getPath());
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void delete_other() {
        User other = new User(2L, "sanjigi", "test", "name", "sanjigi@slipp.net");
        long questionId = getDefaultQuestionId();

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        String url = String.format("/questions/%d", questionId);

        ResponseEntity<String> response = basicAuthTemplate(other)
                                        .postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("response path: {}", response.getHeaders().getLocation().getPath());
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    private long getDefaultQuestionId() {
        return questionRepository.findAll().stream()
                .filter(question -> question.isOwner(defaultUser()))
                .findAny()
                .orElseThrow(EntityNotFoundException::new)
                .getId();
    }
}
