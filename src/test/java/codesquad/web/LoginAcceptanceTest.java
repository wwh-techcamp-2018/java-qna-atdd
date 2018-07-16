package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void login() {
        String userId = "doy";

        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                        .urlEncodedForm()
                        .addParameter("userId", userId)
                        .addParameter("password", "test")
                        .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void loginFail() {
        String userId = "dooho";

        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder
                        .urlEncodedForm()
                        .addParameter("userId", userId)
                        .addParameter("password", "wrong")
                        .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
