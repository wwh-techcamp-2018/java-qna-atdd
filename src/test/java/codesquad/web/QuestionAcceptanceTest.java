package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
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
    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        Question defaultQuestion = new Question("제목1", "내용1");
        defaultQuestion.writeBy(defaultUser());
        question = questionRepository.save(defaultQuestion);
    }

    @Test
    public void create_성공() {
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", question.getTitle())
                .addParameter("contents", question.getContents())
                .build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void read_질문() {
        ResponseEntity<String> response
                = template().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void updateForm_로그인() {
        Question question = questionRepository.findById(this.question.getId())
                .orElseThrow(IllegalArgumentException::new);
        log.debug("question : {}", question);
        ResponseEntity<String> response
                = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
}
