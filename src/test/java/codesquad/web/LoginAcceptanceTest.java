package codesquad.web;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.domain.UserTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;
    private User user;

    @Before
    public void setup() {
        user = UserTest.newUser("userId", "password");
        userRepository.save(user);
    }

    @Test
    public void login() {

        ResponseEntity<String> response = loginRequest(user.getUserId(), user.getPassword());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void fail_userId_mismatch() {

        ResponseEntity<String> response = loginRequest(user.getUserId() + "2", user.getPassword());
        log.debug(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.");
    }


    @Test
    public void fail_password_mismatch() {
        ResponseEntity<String> response = loginRequest(user.getUserId(), user.getPassword() + "2");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.");
    }

    private ResponseEntity<String> loginRequest(String userId, String s) {
        HtmlFormDataBuilder formDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        formDataBuilder.addParameter("userId", userId);
        formDataBuilder.addParameter("password", s);

        return template().postForEntity("/users/login", formDataBuilder.build(), String.class);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.delete(user);
    }
}
