package codesquad.web;

import org.junit.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {

    @Test
    public void login_성공() throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", "javajigi");
        builder.addParameter("password", "test");

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void login_failed_when_user_not_found() {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", "testuser");
        builder.addParameter("password", "password");

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void login_failed_when_mismatch_password() {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", "javajigi");
        builder.addParameter("password", "password");

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
