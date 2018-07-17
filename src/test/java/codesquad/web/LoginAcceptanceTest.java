package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);
    @Autowired
    private UserRepository userRepository;

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void formBuilderSet() {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void login_성공() {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String userId = "javajigi";
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);


        ResponseEntity<String> response = template().postForEntity("/users/in", request, String.class);
        //hint HttpStatus.FOUND > Redirect시엔 FOUND 302 뜨는거 맞다
        log.debug("SuccessHeader!!! : {}", response.getHeaders().toString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        //assertThat(userRepository.findByUserId(userId).isPresent()).isTrue();
        // assertThat(response.getHeaders().containsKey("Set-Cookie")).isTrue();
        //session 검사
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");

    }

    @Test
    public void login_실패() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String userId = "javajigi";
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("password", "fail");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);


        ResponseEntity<String> response = template().postForEntity("/users/in", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        log.debug("FailHeader@@@ : {}", response.getHeaders().toString());
        log.debug("body : {}", response.getBody());
        //assertThat(response.getHeaders().containsKey("Set-Cookie")).isFalse();
        //assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users/login");
    }

    @Test
    public void htmlFormDataBuilder_login성공() {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.addParameter("userId", "javajigi")
                .addParameter("password", "test")
                .build();
        ResponseEntity<String> response = template().postForEntity("/users/in", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void htmlFormDataBuilder_login실패() {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.addParameter("userId", "javajigi")
                .addParameter("password", "fail")
                .build();


        ResponseEntity<String> response = template().postForEntity("/users/in", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
