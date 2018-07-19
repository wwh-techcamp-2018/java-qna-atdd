package codesquad.web;

import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import java.util.UUID;

import static codesquad.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiUserAcceptanceTest extends AcceptanceTest {

    private User newUser;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        newUser = newUser(getRandomName());
    }

    private String getRandomName() {
        return UUID.randomUUID().toString().substring(0, 6);
    }


    @Test
    public void create() throws Exception {
        String location = createResource("/api/users", newUser, User.GUEST_USER);
        User dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, User.class);
        assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        String location = createResource("/api/users", newUser, User.GUEST_USER);
        ResponseEntity<Void> response = basicAuthTemplate().getForEntity(location, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        String location = createResource("/api/users", newUser, User.GUEST_USER);
        User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity = basicAuthTemplate(newUser)
                .exchange(location, HttpMethod.PUT, createHttpEntityWithBody(updateUser), User.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_다른_사람() throws Exception {
        String location = createResource("/api/users", newUser, User.GUEST_USER);
        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");

        ResponseEntity<Void> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntityWithBody(updateUser), Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
