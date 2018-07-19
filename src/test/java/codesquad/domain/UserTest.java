package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    public static final User JAVAJIGI = new User(1L, "javajigi", "test", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "test", "name", "sanjigi@slipp.net");
    private User origin;

    public static User newUser(Long id) {
        return new User(id, "userId", "test", "name", "javajigi@slipp.net");
    }

    public static User newUser(String userId) {
        return newUser(userId, "test");
    }

    public static User newUser(String userId, String password) {
        return new User(0L, userId, password, "name", "javajigi@slipp.net");
    }

    @Before
    public void setUp() throws Exception {
        origin = newUser("sanjigi");
    }

    @Test
    public void update_owner() throws Exception {
        User target = new User("sanjigi", "test", "name2", "javajigi@slipp.net2");
        origin.update(target);
        assertThat(origin.getName()).isEqualTo(target.getName());
        assertThat(origin.getEmail()).isEqualTo(target.getEmail());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        User target = newUser("javajigi", "password");
        origin.update(target);
    }

    @Test
    public void update_match_password() {
        User target = new User("sanjigi", "test", "name2", "javajigi@slipp.net2");
        origin.update(target);
        assertThat(origin.getName()).isEqualTo(target.getName());
        assertThat(origin.getEmail()).isEqualTo(target.getEmail());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_mismatch_password() {
        User target = newUser("sanjigi", "password2");
        origin.update(target);
    }

}
