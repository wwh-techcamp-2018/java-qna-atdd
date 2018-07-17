package codesquad.web;

import codesquad.domain.Question;
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

public class QuestionAcceptanceTest extends AcceptanceTest {

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

        String title = "제목입니다";
        String contents = "본문입니드아ㅏㅏ";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .build();

        ResponseEntity<String> resp = basicAuthTemplate().postForEntity("/questions",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void notLoginCreate() throws Exception {

        String title = "제목입니다";
        String contents = "본문입니드아ㅏㅏ";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .build();


        ResponseEntity<String> resp = basicAuthTemplate(nullUser).postForEntity("/questions",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateQuestion() {
        String title = "[수정]제목입니다";
        String contents = "[수정]본문입니드아ㅏㅏ";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .addParameter("_method", "put")
                .build();

        ResponseEntity<String> resp = basicAuthTemplate().postForEntity("/questions/1",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void notLoginUpdateQuestion() {
        String title = "[수정]제목입니다";
        String contents = "[수정]본문입니드아ㅏㅏ";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .addParameter("_method", "put")
                .build();

        ResponseEntity<String> resp = basicAuthTemplate(nullUser).postForEntity("/questions/1",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    @Test
    public void diffUserUpdateQuestion() {
        String title = "[수정]제목입니다";
        String contents = "[수정]본문입니드아ㅏㅏ";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .addParameter("_method", "put")
                .build();

        User sanjigi = new User("sanjigi", "test", "산지기", "test@example.com");
        ResponseEntity<String> resp = basicAuthTemplate(sanjigi).postForEntity("/questions/1",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteQuestion() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();

        ResponseEntity<String> resp = basicAuthTemplate().postForEntity("/questions/1",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void notLoginDeleteQuestion() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();

        ResponseEntity<String> resp = basicAuthTemplate(nullUser).postForEntity("/questions/1",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void diffUserDeleteQuestion() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();

        User sanjigi = new User("sanjigi", "test", "산지기", "test@example.com");
        ResponseEntity<String> resp = basicAuthTemplate(sanjigi).postForEntity("/questions/1",request, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
