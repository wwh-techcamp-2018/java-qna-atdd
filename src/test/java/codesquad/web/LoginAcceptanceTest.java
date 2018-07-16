package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

public class LoginAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Test
    public void login() {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        String userId = defaultUser().getUserId();
        String password = defaultUser().getPassword();

        builder.addParameter("userId",userId);
        builder.addParameter("password", password);

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath().startsWith("/users"));

    }

    @Test
    public void loginFail() {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        String userId = defaultUser().getUserId();
        String password = "123345";

        builder.addParameter("userId",userId);
        builder.addParameter("password", password);

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}" , response.getBody());
        log.debug("header : {}" , response.getHeaders());
        assertThat(response.getBody()).contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.");
    }

}
