package codesquad.web;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class LoginAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);
    private final String LOGIN_URL = "/users/login";
    Map<String, Object> params;

    @Before
    public void setUp() throws Exception {
        builder = HtmlFormDataBuilder.urlEncodedForm();
        params = new HashMap<>();
    }

    @Test
    public void login() {
        params.put("userId", defaultUser().getUserId());
        params.put("password", defaultUser().getPassword());
        ResponseEntity<String> response = templatePostRequest(LOGIN_URL, params, HtmlFormDataBuilder.METHOD_POST );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/users");
    }

    @Test
    public void loginFail() {
        params.put("userId", defaultUser().getUserId());
        params.put("password", "123456");
        ResponseEntity<String> response = templatePostRequest(LOGIN_URL, params, HtmlFormDataBuilder.METHOD_POST );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.")).isEqualTo(true);
}
