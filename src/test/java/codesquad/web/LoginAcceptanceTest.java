package codesquad.web;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    @Autowired
    UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User(3, "gusdk", "1234", "gusdk1234", "tech_hak@woowahan.com");
    }

    @Test
    public void login() throws Exception {
        ResponseEntity<String> response = template().postForEntity("/users/login", HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", user.getUserId())
                .addParameter("password", user.getPassword())
                .addParameter("name", user.getName())
                .addParameter("email", user.getEmail())
                .build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(userRepository.findByUserId(user.getUserId()).isPresent()).isTrue();
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void login_No() throws Exception {
        ResponseEntity<String> response = template().postForEntity("/users/login", HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", user.getUserId())
                .addParameter("password", "4321")
                .addParameter("name", user.getName())
                .addParameter("email", user.getEmail())
                .build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
