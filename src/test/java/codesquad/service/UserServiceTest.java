package codesquad.service;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @Before
    public void setUp() throws Exception {
        user = UserTest.SANJIGI;
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }

    @Test
    public void login_success() throws Exception {
        User loginUser = userService.login(user.getUserId(), user.getPassword());
        assertThat(loginUser).isEqualTo(user);
    }

    @Test(expected = UnAuthenticationException.class)
    public void login_failed_when_user_not_found() throws Exception {
        when(userRepository.findByUserId("ksy")).thenReturn(Optional.empty());
        userService.login("ksy", "password");
    }

    @Test(expected = UnAuthenticationException.class)
    public void login_failed_when_mismatch_password() throws Exception {
        userService.login(user.getUserId(), user.getPassword() + "2");
    }

    @Test
    public void update_success() throws Exception {
        User updateUser = new User("sanjigi", "test", "update_name", "sanjigi@slipp.net2");
        user = userService.update(user, 2L, updateUser);
        assertThat(user.getName()).isEqualTo(updateUser.getName());
        assertThat(user.getEmail()).isEqualTo(updateUser.getEmail());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_failed_when_not_user() throws Exception {
        User updateUser = new User("ksy", "test", "update_name", "sanjigi@slipp.net2");
        userService.update(user, user.getId(), updateUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_failed_when_user_guest() throws Exception {
        userService.update(user, user.getId(), User.GUEST_USER);
    }
}
