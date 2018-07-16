package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 로그인_성공() throws Exception {
        String userId = "javajigi";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void 로그인_실패() throws Exception {
        String userId = "javajigi";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", "test123")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
