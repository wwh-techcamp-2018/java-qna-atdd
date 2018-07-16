package codesquad.web;

import support.builder.HtmlFormDataBuilder;
import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void setUp() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodeForm();
    }

    @Test
    public void login() throws Exception {
        String userId = "sanjigi";
        htmlFormDataBuilder.addParameter("userId", userId);
        htmlFormDataBuilder.addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request =htmlFormDataBuilder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(userRepository.findByUserId(userId).isPresent()).isTrue();
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }


}
