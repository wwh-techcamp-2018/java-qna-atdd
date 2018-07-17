package codesquad.web;

import codesquad.domain.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptaceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    private List<Question> questions;
    private Question question;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private HtmlFormDataBuilder formDataBuilder;

    @Before
    public void setUp() throws Exception {
        formDataBuilder = HtmlFormDataBuilder.urlEncodedForm();

        questionRepository.deleteAll();
        List<Question> questionList = QuestionTest.questionList();
        for (Question question : questionList) {
            question.writeBy(defaultUser());
        }

        questionRepository.saveAll(questionList);
        questions = questionRepository.findAll();
        question = questions.get(0);

    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        for (Question question : questions) {
            assertThat(response.getBody()).contains(question.getTitle());
        }
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void showQuestionDetail() {
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/%d", question.getId()), String.class);

        String body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body).contains(question.getTitle());
        assertThat(body).contains(question.getContents());
        log.debug("body : {}", body);
    }

    @Test
    public void questionDetailFail() {
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/%d", 0L), String.class);

        log.debug("body : {}", response.getBody());
        log.debug("header : {}", response.getHeaders());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void createForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void createForm() {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() {
        User loginUser = defaultUser();

        createQuestionParam();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", formDataBuilder.build(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        assertThat(questionRepository.findByDeleted(false)).hasSize(questions.size()+1);
    }

    @Test
    public void create_no_login() {
        createQuestionParam();

        ResponseEntity<String> response = template().postForEntity("/questions",
                formDataBuilder.build(),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response =
                template().getForEntity(
                        String.format("/questions/%d/form", question.getWriter().getId()),
                        String.class
                );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response =
                basicAuthTemplate().getForEntity(
                        String.format("/questions/%d/form", question.getId()),
                        String.class
                );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update() {
        ResponseEntity<String> response = update(basicAuthTemplate());

        Question updated = questionRepository.findById(question.getId()).get();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith(question.generateUrl());

        assertThat(updated.getTitle()).isEqualTo(getNewTitle());
        assertThat(updated.getContents()).isEqualTo(getNewContents());

    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> update(TestRestTemplate testTemplate) {
        formDataBuilder.addParameter("title", getNewTitle())
                .addParameter("contents", getNewContents())
                .put();

        return testTemplate.postForEntity(question.generateUrl(), formDataBuilder.build(), String.class);
    }

    @Test
    public void delete() {
        formDataBuilder.delete();

        ResponseEntity<String> response =
                basicAuthTemplate().postForEntity(
                        String.format("/questions/%d", question.getId()),
                        formDataBuilder.build(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");

        assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isTrue();
    }



    @Test
    public void delete_no_login() {
        formDataBuilder.delete();

        ResponseEntity<String> response =
                template().postForEntity(
                        String.format("/questions/%d", question.getId()),
                        formDataBuilder.build(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private void createQuestionParam() {
        formDataBuilder.addParameter("title", "새로운 제목")
                .addParameter("contents", "새로운 내용");
    }

    private String getNewTitle() {
        return question.getTitle() + "1";
    }

    private String getNewContents() {
        return question.getContents() + "1";
    }

}
