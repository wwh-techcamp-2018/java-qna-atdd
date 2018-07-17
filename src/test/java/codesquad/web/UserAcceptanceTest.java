package codesquad.web;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);
    private HtmlFormDataBuilder htmlFormDataBuilder;
    @Autowired
    private UserRepository userRepository;

    @Before
    public void fromBuilderSet() {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }
    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/users/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String userId = "testuser";
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("password", "password");
        params.add("name", "자바지기");
        params.add("email", "javajigi@slipp.net");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        ResponseEntity<String> response = template().postForEntity("/users", request, String.class);
        //hint HttpStatus.FOUND > Redirect시엔 FOUND 302 뜨는거 맞다
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(userRepository.findByUserId(userId).isPresent()).isTrue();
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void create_formBuilder() throws Exception {
        String userId = "testuser2";
        htmlFormDataBuilder.addParameter("userId", userId)
                .addParameter("password", "password")
                .addParameter("name", "자아바지기")
                .addParameter("email", "jaaavajigi@slipp.net");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = template().postForEntity("/users", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(userRepository.findByUserId(userId).isPresent()).isTrue();
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/users", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody()).contains(defaultUser().getEmail());
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/users/%d/form", defaultUser().getId()),
                String.class);
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
//        ResponseEntity<String> response = update(template());
        ResponseEntity<String> response = update_formBuilder(template());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("_method", "put");
        params.add("password", "test");
        params.add("name", "자바지기2");
        params.add("email", "javajigi@slipp.net");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        return template.postForEntity(String.format("/users/%d", defaultUser().getId()), request, String.class);
    }

    private ResponseEntity<String> update_formBuilder(TestRestTemplate template) throws Exception {
        htmlFormDataBuilder.addParameter("_method", "put")
                .addParameter("password", "test")
                .addParameter("name", "자바지기2")
                .addParameter("email", "javajigi@slipp.net");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        //String.format("/users/%d", defaultUser().getId())
        return template.postForEntity(String.format("/users/%d", defaultUser().getId()), request, String.class);
    }

    @Test
    public void update() throws Exception {
//        ResponseEntity<String> response = update(basicAuthTemplate());
        ResponseEntity<String> response = update_formBuilder(basicAuthTemplate());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }
}
