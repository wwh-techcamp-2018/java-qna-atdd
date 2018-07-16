package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.security.HttpSessionUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.util.HtmlFormDataBuilder;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    private HtmlFormDataBuilder htmlFormDataBuilder;


    @Before
    public void setUp() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void login() throws Exception {
        String userId = "javajigi";
        String password = "test";

        htmlFormDataBuilder.addParameter("userId", userId);
        htmlFormDataBuilder.addParameter("password", password);

        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormDataBuilder.build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void login_failed_not_match_user_id() throws Exception {
        String userId = "tester";
        String password = "test";
        htmlFormDataBuilder.addParameter("userId", userId);
        htmlFormDataBuilder.addParameter("password", password);

        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormDataBuilder.build(), String.class);

        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users/login_failed");
    }

    @Test
    public void login_failed_not_match_password() throws Exception {
        String userId = "javajigi";
        String password = "abc";

        htmlFormDataBuilder.addParameter("userId", userId);
        htmlFormDataBuilder.addParameter("password", password);

        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormDataBuilder.build(), String.class);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users/login_failed");
    }
}

