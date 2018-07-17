package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    @Autowired
    private QuestionRepository questionRepository;
    private User unAuthorizedUser;

    @Before
    public void setUp() throws Exception {
        unAuthorizedUser = new User();
        unAuthorizedUser.setUserId("sanjigi");
        unAuthorizedUser.setPassword("test");
    }

    @Test
    public void read_list_guestAndLoginUser() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        String qnaListTag = "<div class=\"panel panel-default qna-list\">";
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(qnaListTag);
    }

    @Test
    public void create_loginUser() {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("title", "첫번째 제목입니다.")
                .addParameter("contents", "첫번째 내용입니다.");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);
        assertThat(questionRepository.findById(3L).get().getTitle()).isEqualTo("첫번째 제목입니다.");
    }

    @Test
    public void read_detail_guestAndLoginUser() {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        String title = "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?";
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(title);
    }

    @Test
    public void update_guest() {
        ResponseEntity<String> response = update(template());
        assertThat(response.getBody()).contains("사용자 아이디");
    }

    @Test
    public void update_loginUser_Owner() {
        ResponseEntity<String> response = update(basicAuthTemplate(defaultUser()));
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/questions/1");
        response = basicAuthTemplate(defaultUser()).getForEntity("/questions/1", String.class);
        assertThat(response.getBody()).contains("수정하는 글이다");
    }

    @Test
    public void update_loginUser_NotOwner() {
        ResponseEntity<String> response = update(basicAuthTemplate(unAuthorizedUser));
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        response = basicAuthTemplate(unAuthorizedUser).getForEntity("/questions/1", String.class);
        assertThat(response.getBody()).contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?");
    }

    @Test
    public void delete_guest() {
        ResponseEntity<String> response = delete(template());
        assertThat(response.getBody()).contains("사용자 아이디");
    }

    @Test
    public void delete_loginUser_Owner() {
        ResponseEntity<String> response = delete(basicAuthTemplate());
        assertThat(questionRepository.findById(1L).get().isDeleted()).isEqualTo(true);
    }

    @Test
    public void delete_loginUser_NotOwner() {
        ResponseEntity<String> response = delete(basicAuthTemplate(unAuthorizedUser));
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        response = basicAuthTemplate(unAuthorizedUser).getForEntity("/questions/1", String.class);
        assertThat(response.getBody()).contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?");
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "수정하는 글이다")
                .addParameter("contents", "사랑해요 포비<3")
                .build();
        return template.postForEntity("/questions/1", request, String.class);
    }

    private ResponseEntity<String> delete(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
        return template.postForEntity("/questions/1", request, String.class);
    }
}
