package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.BasicAuthInterceptor;
import codesquad.service.QnaService;
import codesquad.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.builder.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerAcceptanceTest extends AcceptanceTest {

    @Mock
    private UserService userService;

    @Autowired
    private QnaService qnaService;

    @InjectMocks
    private BasicAuthInterceptor basicAuthInterceptor;
    private User nullUser;

    @Before
    public void setUp() throws Exception {
        nullUser = new User(null, null, null, null);
    }

    @Test
    public void loginUserCreate() throws Exception {
        String contents = "본문입니드아ㅏㅏ";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", contents)
                .build();

        ResponseEntity<String> resp = basicAuthTemplate().postForEntity("/questions/1/answer",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void notLoginUserCreate() throws Exception {
        String contents = "본문입니드아ㅏㅏ";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", contents)
                .build();

        ResponseEntity<String> resp = basicAuthTemplate(nullUser).postForEntity("/questions/1/answer",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteAnswer() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();

        ResponseEntity<String> resp = basicAuthTemplate().postForEntity("/questions/1/answer/1",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void notLoginDeleteAnswer() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();

        ResponseEntity<String> resp = basicAuthTemplate(nullUser).postForEntity("/questions/1/answer/1",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void diffUserDeleteAnswer() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();

        User sanjigi = new User("sanjigi", "test", "산지기", "test@example.com");
        ResponseEntity<String> resp = basicAuthTemplate(sanjigi).postForEntity("/questions/1/answer/1",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
