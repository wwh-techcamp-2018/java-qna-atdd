package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.ApiAcceptanceTest;

import static codesquad.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiUserAcceptanceTest extends ApiAcceptanceTest {

    @Test
    public void create() throws Exception {
        User newUser = newUser("testuser1");
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        User dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, User.class);
        assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        User newUser = newUser("testuser2");
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        response = basicAuthTemplate(defaultUser()).exchange(location,HttpMethod.GET,createHttpEntity(), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        User newUser = newUser("testuser3");
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User original = basicAuthTemplate(newUser).getForObject(location, User.class);

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
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
