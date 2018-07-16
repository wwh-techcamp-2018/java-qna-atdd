package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.builder.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);


    @Test
    public void loginForm() {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void login() {
        String userId = "javajigi";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("userId", userId)
                .addParameter("password", "test")
                .build();
        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void loginFailed() {
        String userId = "javajigi";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("userId", userId)
                .addParameter("password", "1234")
                .build();
        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.");
    }
}
