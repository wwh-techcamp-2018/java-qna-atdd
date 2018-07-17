package support.test;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    public TestRestTemplate template() {
        return template;
    }

    public TestRestTemplate basicAuthTemplate() {
        return basicAuthTemplate(defaultUser());
    }

    public TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    protected User defaultUser() {
        return findByUserId(DEFAULT_LOGIN_USER);
    }

    protected User getUserByUserId(String userId) {
        return findByUserId(userId);
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }


    protected <T> ResponseEntity<T> createResource(String path, Object bodyPayload, Class<T> classType) {
        ResponseEntity<T> response = basicAuthTemplate().postForEntity(path, bodyPayload, classType);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        return response;
    }

    protected <T> ResponseEntity<T> createResourceBySpecificUser(String path, Object bodyPayload, Class<T> classType, User loginUser) {
        ResponseEntity<T> response = basicAuthTemplate(loginUser).postForEntity(path, bodyPayload, classType);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        return response;
    }

    protected <T> T getResourceBySpecificUser(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    protected <T> T getResourceByDefaultUser(String location, Class<T> responseType) {
        return basicAuthTemplate().getForObject(location, responseType);
    }

    protected <T> ResponseEntity<T> updateResourceBySpecificUser(User user, String location, HttpEntity httpEntity, Class<T> responseType) {
        return basicAuthTemplate(user).exchange(location, HttpMethod.PUT, httpEntity, responseType);
    }

    protected <T> ResponseEntity<T> updateResourceByDefaultUser(String location, HttpEntity httpEntity, Class<T> responseType) {
        return basicAuthTemplate().exchange(location, HttpMethod.PUT, httpEntity, responseType);
    }

    protected <T> ResponseEntity<T> deleteResourceBySpecificUser(User user, String location, HttpEntity httpEntity, Class<T> responseType) {
        return basicAuthTemplate(user).exchange(location, HttpMethod.DELETE, httpEntity, responseType);
    }

    protected <T> ResponseEntity<T> deleteResourceByDefaultUser(String location, HttpEntity httpEntity, Class<T> responseType) {
        return basicAuthTemplate().exchange(location, HttpMethod.DELETE, httpEntity, responseType);
    }


}
