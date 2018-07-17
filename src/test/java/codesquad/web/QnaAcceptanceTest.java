package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.util.HtmlFormDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class QnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QnaAcceptanceTest.class);

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void createForm_login() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("로그인 후 이용 부탁");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("title", "title1")
                .addParameter("contents", "contents1")
                .build();

        User loginUser = defaultUser();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/");
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody()).contains(defaultQuestion().getTitle());
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity(defaultQuestion().generateUrl() + "/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("로그인 후 이용 부탁");
    }

    @Test
    public void updateForm_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(defaultQuestion().generateUrl() + "/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(defaultQuestion().getTitle());
        assertThat(response.getBody()).contains(defaultQuestion().getContents());
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("로그인 후 이용 부탁");
    }

    @Test
    public void update_not_own() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate(otherUser()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("접근 권한이 없음");
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        htmlFormDataBuilder.addParameter("_method", "put");
        htmlFormDataBuilder.addParameter("title", "test2");
        htmlFormDataBuilder.addParameter("contents", "contents2");

        return template.postForEntity(defaultQuestion().generateUrl(), htmlFormDataBuilder.build(), String.class);
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(defaultQuestion().generateUrl());
    }

    @Test
    public void delete_no_login() {
        ResponseEntity<String> response = template().exchange(defaultQuestion().generateUrl(), HttpMethod.DELETE, htmlFormDataBuilder.build(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("로그인 후 이용 부탁");
    }

    @Test
    public void delete_not_own() {
        ResponseEntity<String> response = basicAuthTemplate(otherUser()).exchange(defaultQuestion().generateUrl(), HttpMethod.DELETE, htmlFormDataBuilder.build(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("접근 권한이 없음");
    }

    @Test
    public void delete() {
        Question deletedQuestion = new Question("deleted", "deleted contents");
        deletedQuestion.writeBy(defaultUser());

        questionRepository.save(deletedQuestion);

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange(deletedQuestion.generateUrl(), HttpMethod.DELETE, htmlFormDataBuilder.build(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }
}
