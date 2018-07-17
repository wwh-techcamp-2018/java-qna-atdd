package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.helper.JsonDataBuilder;
import support.test.AcceptanceTest;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    public static final User other = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private User writer;
    private Question question;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        writer = defaultUser();
        question = new Question("(created title)", "(created contents)");
    }

    @Test
    public void create() throws Exception {
        String location = createResource("/api/questions", question, basicAuthTemplate(writer), Void.class);
        Question target = getResource(location, Question.class, writer);
        assertThat(target).isNotNull();
    }

    @Test
    public void show_인증없이() throws Exception {
        String url = String.format("/api/questions/%d", getDefaultQuestionId());
        Question target = template().getForObject(url, Question.class);
        assertThat(target).isNotNull();
    }

    @Test
    public void update() throws Exception {
        String location = createResource("/api/questions", question, basicAuthTemplate(writer), Void.class);
        Question original = getResource(location, Question.class, writer);

        Question updateQuestion = new Question(original.getId(), writer, "(updated title)", "(updated contents)");

        ResponseEntity<Question> responseEntity = basicAuthTemplate(writer)
                .exchange(location, HttpMethod.PUT, JsonDataBuilder.createHttpEntity(updateQuestion), Question.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_다른_사람() throws Exception {
        String location = createResource("/api/questions", question, basicAuthTemplate(writer), Void.class);
        Question original = getResource(location, Question.class, writer);

        Question updateQuestion = new Question(original.getId(), writer, "(updated title)", "(updated contents)");

        ResponseEntity<Void> responseEntity = basicAuthTemplate(other)
                .exchange(location, HttpMethod.PUT, JsonDataBuilder.createHttpEntity(updateQuestion), Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_게스트() throws Exception {
        String location = createResource("/api/questions", question, basicAuthTemplate(writer), Void.class);
        Question original = getResource(location, Question.class, writer);

        Question updateQuestion = new Question(original.getId(), writer, "(updated title)", "(updated contents)");

        ResponseEntity<Void> responseEntity = template()
                .exchange(location, HttpMethod.PUT, JsonDataBuilder.createHttpEntity(updateQuestion), Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() throws Exception {
        String location = createResource("/api/questions", question, basicAuthTemplate(writer), Void.class);

        ResponseEntity<Void> deleteResponse = basicAuthTemplate(writer)
                .exchange(location, HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void delete_다른_사용자() throws Exception {
        String location = createResource("/api/questions", question, basicAuthTemplate(writer), Void.class);

        ResponseEntity<Void> deleteResponse = basicAuthTemplate(other)
                .exchange(location, HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private long getDefaultQuestionId() {
        return questionRepository.findAll().stream()
                .filter(question -> question.isOwner(defaultUser()))
                .findAny()
                .orElseThrow(EntityNotFoundException::new)
                .getId();
    }
}
