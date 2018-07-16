package codesquad.web;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);
    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void setUp() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }
    @Test
    public void login() {

        String userId = "javajigi";
        htmlFormDataBuilder.addParameter("userId", userId);
        htmlFormDataBuilder.addParameter("password", "test");

        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormDataBuilder.build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");

    }

    @Test
    public void login_failed() {
        String userId = "javajigi";
        htmlFormDataBuilder.addParameter("userId", userId);
        htmlFormDataBuilder.addParameter("password", "test112");

        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormDataBuilder.build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

