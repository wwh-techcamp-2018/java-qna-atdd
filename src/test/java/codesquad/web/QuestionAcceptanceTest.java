package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.builder.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Test
    public void create() {
        TestRestTemplate template = basicAuthTemplate();
        HttpEntity<MultiValueMap<String, Object>> request =
            HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("title", "너무 너무 궁금해~ㅠㅠ")
                .addParameter("contents", "오늘 점심 무얼 먹어야 할까요..ㅠㅠㅠㅠ 넘 고민 ㅠㅠㅠㅠㅠ")
                .build();

        ResponseEntity<String> response = template.postForEntity("/qnas", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/qnas");
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/qnas", String.class);
        log.debug("body: {}", response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void show() {
        ResponseEntity<String> response = template().getForEntity("/qnas/1", String.class);
        log.debug("body: {}", response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/qnas/form/1", String.class);
        log.debug("body : {}", response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/qnas/form/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요");
    }

    @Test
    public void updateForm_not_matched() {
        ResponseEntity<String> response = basicAuthTemplate(new User("unknown", "", "", ""))
                .getForEntity("/qnas/form/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() {
        TestRestTemplate template = basicAuthTemplate();
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                    .urlEncodedForm()
                    .addParameter("_method", "put")
                    .addParameter("title", "곧 있음 점심")
                    .addParameter("contents", "과연 3가지 후보 중에 무엇을 먹게될까요?!")
                    .build();
        ResponseEntity<String> response = template.postForEntity("/qnas/1", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/qnas");
    }

    @Test
    public void delete() {
        TestRestTemplate template = basicAuthTemplate();
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                    .urlEncodedForm()
                    .addParameter("_method", "delete")
                    .build();
        ResponseEntity<String> response = template.postForEntity("/qnas/2", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/qnas");
    }

    @Test
    public void delete_no_login() {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                        .urlEncodedForm()
                        .addParameter("_method", "delete")
                        .build();
        ResponseEntity<String> response = template().postForEntity("/qnas/1", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_not_matched() {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                        .urlEncodedForm()
                        .addParameter("_method", "delete")
                        .build();
        User wrongUser = new User("sanjigi", "1", "", "");
        ResponseEntity<String> response = basicAuthTemplate(wrongUser).postForEntity("/qnas/1", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
