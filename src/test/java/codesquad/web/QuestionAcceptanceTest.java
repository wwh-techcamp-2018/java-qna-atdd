package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private Question question;
    private User otherUser;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        Question defaultQuestion = new Question("제목1", "내용1");
        defaultQuestion.writeBy(defaultUser());
        question = questionRepository.save(defaultQuestion);

        otherUser = new User("whddhks", "1234", "홍종완", "tech_jwh@woowahan.com");
    }

    @Test
    public void create_성공() {
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", question.getTitle())
                .addParameter("contents", question.getContents())
                .build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void create_실패_로그인_안_했을_떄() {
        ResponseEntity<String> response = template().postForEntity("/questions", HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", question.getTitle())
                .addParameter("contents", question.getContents())
                .build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void read_질문리스트() {
        ResponseEntity<String> response
                = template().getForEntity("/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void read_질문상세보기() {
        ResponseEntity<String> response
                = template().getForEntity(String.format("/questions/%d", question.getId()), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void updateForm_로그인_성공() {
        Question question = questionRepository.findById(this.question.getId())
                .orElseThrow(IllegalArgumentException::new);
        log.debug("question : {}", question);

        ResponseEntity<String> response
                = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateForm_로그인_안_했을_때() {
        Question question = questionRepository.findById(this.question.getId())
                .orElseThrow(IllegalArgumentException::new);
        log.debug("question : {}", question);

        ResponseEntity<String> response
                = template().getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm_타인_질문_요청() {
        Question question = questionRepository.findById(this.question.getId())
                .orElseThrow(IllegalArgumentException::new);
        log.debug("question : {}", question);

        ResponseEntity<String> response
                = basicAuthTemplate(otherUser)
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_성공() {
        question.setTitle("홍종완");
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(String.format("/questions/%d", question.getId()), HtmlFormDataBuilder.urlEncodedForm()
                        .put()
                        .addParameter("title", question.getTitle())
                        .addParameter("contents", question.getContents())
                        .build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(questionRepository.findById(question.getId())
                .orElseThrow(UnAuthorizedException::new)
                .getTitle())
                .isEqualTo(question.getTitle());
    }

    @Test
    public void update_로그인_안한_사용자() {
        question.setTitle("홍종완");
        ResponseEntity<String> response = template()
                .postForEntity(String.format("/questions/%d", question.getId()), HtmlFormDataBuilder.urlEncodedForm()
                        .put()
                        .addParameter("title", question.getTitle())
                        .addParameter("contents", question.getContents())
                        .build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_타인글() {
        question.setTitle("홍종완");
        ResponseEntity<String> response = basicAuthTemplate(otherUser)
                .postForEntity(String.format("/questions/%d", question.getId()), HtmlFormDataBuilder.urlEncodedForm()
                        .put()
                        .addParameter("title", question.getTitle())
                        .addParameter("contents", question.getContents())
                        .build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_질문() {
        ResponseEntity<String> response
                = basicAuthTemplate()
                .postForEntity(String.format("/questions/%d", question.getId()), HtmlFormDataBuilder.urlEncodedForm()
                        .delete().build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(questionRepository.findById(question.getId())
                .orElseThrow(UnAuthorizedException::new)
                .getTitle())
                .isEqualTo(question.getTitle());
    }

    @Test
    public void delete_로그인_안한_사용자() {
        ResponseEntity<String> response
                = template().postForEntity(String.format("/questions/%d", question.getId()), HtmlFormDataBuilder.urlEncodedForm()
                        .delete().build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_타인글() {
        ResponseEntity<String> response
                = basicAuthTemplate(otherUser).postForEntity(String.format("/questions/%d", question.getId()), HtmlFormDataBuilder.urlEncodedForm()
                        .delete().build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
