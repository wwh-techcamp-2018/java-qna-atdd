package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiUserAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() throws Exception {
        User newUser = newUser("testuser1");
        String location = createResource("/api/users", newUser);
        User dbUser = getResource(location, User.class, findByUserId(newUser.getUserId()));
        assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        User newUser = newUser("testuser2");
        String location = createResource("/api/users", newUser);

        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).getForEntity(location, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        User newUser = newUser("testuser3");
        String location = createResource("/api/users", newUser);

        User original = getResource(location, User.class, newUser);
        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity =
                basicAuthTemplate(newUser).exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), User.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_다른_사람() throws Exception {
        User newUser = newUser("testuser4");
        String location = createResource("/api/users", newUser);
        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
