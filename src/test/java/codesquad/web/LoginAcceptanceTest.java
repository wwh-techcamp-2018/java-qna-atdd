package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Test
    public void login() throws Exception {
        String userId = "sanjigi";

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("userId", userId)
                .addParameter("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(findByUserId(userId).getUserId()).isEqualTo(userId);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }


}
