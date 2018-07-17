package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/users/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        String userId = "testuser";
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.addParameter("userId", userId)
                .addParameter("password", "password")
                .addParameter("name", "자바지기")
                .addParameter("email", "javajigi@slipp.net")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(findByUserId(userId).getUserId()).isEqualTo(userId);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/users", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(defaultUser().getEmail());
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/users/%d/form", defaultUser().getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/users/%d/form", loginUser.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(defaultUser().getEmail());
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.put()
                .addParameter("userId", "javajigi")
                .addParameter("password", "test")
                .addParameter("name", "자바지기2")
                .addParameter("email", "javajigi@slipp.net")
                .build();

        return template.postForEntity(String.format("/users/%d", defaultUser().getId()), request, String.class);
    }

}
