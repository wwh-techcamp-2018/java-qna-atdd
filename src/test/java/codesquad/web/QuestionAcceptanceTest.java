package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);
    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Autowired
    QuestionRepository questionRepository;

    Question defaultQuestion;

    @Before
    public void setUp() throws Exception {
         htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
         defaultQuestion = questionRepository.findById(1L).get();
    }

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {

        String title = "test title" + System.currentTimeMillis();
        htmlFormDataBuilder.addParameter("title", title);
        htmlFormDataBuilder.addParameter("contents", "test contents");


        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", htmlFormDataBuilder.build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");

        response = template().getForEntity("/", String.class);
        assertThat(response.getBody().contains(title)).isTrue();
    }

    @Test
    public void showTest() throws Exception {
        Question question = questionRepository.findById(1L).get();

        ResponseEntity<String> response = template().getForEntity("/questions/"+question.getId(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().contains(question.getTitle())).isTrue();
        assertThat(response.getBody().contains(question.getContents())).isTrue();
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", defaultQuestion.getId()),String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm_no_writer() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(new User("testuser","password","name", "email@a.a"))
                .getForEntity(String.format("/questions/%d/form", defaultQuestion.getId()),String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm_writer() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                    .getForEntity(String.format("/questions/%d/form", defaultQuestion.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().contains(defaultQuestion.getContents())).isTrue();
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {

        htmlFormDataBuilder.put()
            .addParameter("title", "test title update")
            .addParameter("contents", "test contents update");
        return template.postForEntity(String.format("/questions/%d", defaultQuestion.getId()), htmlFormDataBuilder.build(), String.class);
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/"+defaultQuestion.getId());
    }

    public ResponseEntity<String> deleteSetUp(TestRestTemplate template) {
        htmlFormDataBuilder.delete();
        return template.postForEntity(String.format("/questions/%d", defaultQuestion.getId()), htmlFormDataBuilder.build(), String.class);
    }

    @Test
    public void delete_no_login() throws Exception {
        ResponseEntity<String> response = deleteSetUp(template());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_no_writer() throws Exception {
        ResponseEntity<String> response = deleteSetUp(basicAuthTemplate(new User("testuser","password","name", "email@a.a")));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() throws Exception {
        ResponseEntity<String> response = deleteSetUp(basicAuthTemplate());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }
}
