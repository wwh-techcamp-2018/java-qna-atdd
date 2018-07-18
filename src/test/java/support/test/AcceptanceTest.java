package support.test;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.web.HtmlFormDataBuilder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    protected HtmlFormDataBuilder builder;

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

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }


    protected ResponseEntity<String> templatePostRequest(String url, Map params, String method) {
        HttpEntity<MultiValueMap<String, Object>> request = makeRequest(method, params);
        return template()
                .postForEntity(url, request, String.class);
    }

    protected ResponseEntity<String> basicAuthPostRequest(String url, User user, Map params, String method) {
        HttpEntity<MultiValueMap<String, Object>> request = makeRequest(method, params);
        return basicAuthTemplate(user)
                .postForEntity(url, request, String.class);
    }

    protected HttpEntity<MultiValueMap<String, Object>> makeRequest(String method, Map params) {
        return builder
                .method(method)
                .addParameters(params)
                .bulid();
    }
}
