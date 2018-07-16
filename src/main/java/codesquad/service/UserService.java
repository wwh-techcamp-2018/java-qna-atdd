package codesquad.service;

import codesquad.exception.UnAuthenticationException;
import codesquad.exception.UnAuthorizedException;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.web.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("userService")
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    public User add(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(User loginUser, long id, User updatedUser) {
        User original = findById(loginUser, id);
        original.update(loginUser, updatedUser);
        return original;
    }

    public User findById(User loginUser, long id) {
        return userRepository.findById(id)
                .filter(user -> user.equals(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User login(String userId, String password) throws UnAuthenticationException {
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@222");
        User loginUser = userRepository.findByUserId(userId).orElseThrow(UnAuthenticationException::new);
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
        if(!loginUser.verifyPassword(password)){
            log.info("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
            throw new UnAuthenticationException();
        }
        return loginUser;
    }
}
