package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createQuestionForm() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void createQuestion() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "title_good")
                .addParameter("contents", "long_long_contents")
                .build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void questionList() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        for (Question question : questionRepository.findByDeleted(false)) {
            assertThat(response.getBody()).contains(question.getTitle());
        }
    }

    @Test
    public void showQuestion() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Question question = questionRepository.findByIdAndDeletedFalse((long) 1).orElseThrow(Exception::new);
        assertThat(response.getBody()).contains(question.getTitle()).contains(question.getWriter().getName());
    }

    @Test
    public void updateQuestionForm() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity("/questions/1/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Question question = questionRepository.findByIdAndDeletedFalse((long) 1).orElseThrow(Exception::new);
        assertThat(response.getBody()).contains(question.getTitle()).contains(question.getContents());
    }

    @Test
    public void updateQuestionFormFail() {
        ResponseEntity<String> response = basicAuthTemplate(userRepository.findByUserId("yeon").get())
                .getForEntity("/questions/1/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateQuestion() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "title_update")
                .addParameter("contents", "long_long_contents_update")
                .build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions/1", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/questions/1");
    }

    @Test
    public void deleteQuestion() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions/1", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }
}
