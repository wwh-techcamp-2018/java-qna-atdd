package codesquad.web;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.util.HttpRequestGenerator;

import static org.assertj.core.api.Assertions.assertThat;


public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User("arrata", "12345", "jhsdfsdf","sdfsdf@asdsfds");
        userRepository.save(user);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.delete(user);
    }

    @Test
    public void login_success() {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", user.getUserId());
        params.add("password", user.getPassword());

        HttpEntity<MultiValueMap<String, Object>> request = HttpRequestGenerator.fetchRequest(params);

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        log.debug("status code: {}", response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/users");


    }

    @Test
    public void login_failure() {

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", "arratago");
        params.add("password", user.getPassword());

        HttpEntity<MultiValueMap<String, Object>> request = HttpRequestGenerator.fetchRequest(params);

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        log.debug("status code: {}", response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/users/login_failed");
    }
}
