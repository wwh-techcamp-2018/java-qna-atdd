package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    QuestionRepository questionRepository;

    private Question question;
    private Question updateQuestion;
    private Answer answer;

    @Before
    public void setup(){
        question = new Question("questionTitle", "questionContents");
        question.writeBy(defaultUser());
        questionRepository.save(question);

        updateQuestion = new Question("Update questionTitle", "Update questionContents");
        question.writeBy(defaultUser());

        answer = new Answer();
        answer.setContents("answerContents");
    }

    @After
    public void tearDown(){
        questionRepository.delete(question);
    }


    @Test
    public void create() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
                .postForEntity("/api/questions",question, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String location = responseEntity.getHeaders().getLocation().getPath();
        assertThat(location).isEqualTo("/api" + question.generateUrl());

        Question saveQuestion = basicAuthTemplate(defaultUser())
                .getForObject(location, Question.class);

        assertThat(saveQuestion.getTitle()).isEqualTo(question.getTitle());
        assertThat(saveQuestion.getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void create_invalid_user(){
        ResponseEntity<Void> responseEntity = basicAuthTemplate(User.GUEST_USER)
                .postForEntity("/api/questions",question, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update(){
        ResponseEntity<Question> dbQuestion = basicAuthTemplate(defaultUser())
                .exchange("/api" + question.generateUrl()
                        , HttpMethod.PUT
                        , createHttpEntity(updateQuestion)
                        , Question.class);
        assertThat(dbQuestion.getBody().getTitle()).isEqualTo(updateQuestion.getTitle());
        assertThat(dbQuestion.getBody().getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test
    public void update_invalid_user(){
        ResponseEntity<Question> dbQuestion = basicAuthTemplate(User.GUEST_USER)
                .exchange("/api" + question.generateUrl()
                        , HttpMethod.PUT
                        , createHttpEntity(updateQuestion)
                        , Question.class);
        assertThat(dbQuestion.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete(){
        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
                .exchange("/api" + question.generateUrl()
                        , HttpMethod.DELETE
                        ,createHttpEntity(null)
                        , Void.class);
        assertThat(responseEntity.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void delete_invalid_user(){
        ResponseEntity<Void> responseEntity = basicAuthTemplate(User.GUEST_USER)
                .exchange("/api" + question.generateUrl()
                        , HttpMethod.DELETE
                        ,createHttpEntity(null)
                        , Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void addAnswer(){
        Answer saveAnswer = basicAuthTemplate(defaultUser())
                .postForObject("/api" + question.generateUrl() + "/answers"
                        , answer
                        , Answer.class);
        assertThat(saveAnswer.getContents()).isEqualTo(answer.getContents());
    }



    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }

}