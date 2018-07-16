package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.builder.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class LoginAcceptanceTest extends AcceptanceTest {

    @Test
    public void login() {
        User user = new User("javajigi", "test", "Ryunhee Han", "ryuneeee@gmail.com");

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", user.getUserId())
                .addParameter("password", user.getPassword())
                .addParameter("name", user.getName())
                .addParameter("email", user.getEmail())
                .build();

        ResponseEntity<String> resp = template().postForEntity("/users/login", request, String.class);
        assertEquals(HttpStatus.FOUND, resp.getStatusCode());
    }

    @Test
    public void login_failed() {
        User user = new User("javajigi", "test2", "Ryunhee Han", "ryuneeee@gmail.com");

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", user.getUserId())
                .addParameter("password", user.getPassword())
                .addParameter("name", user.getName())
                .addParameter("email", user.getEmail())
                .build();

        ResponseEntity<String> resp = template().postForEntity("/users/login", request, String.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요."));
    }
}
