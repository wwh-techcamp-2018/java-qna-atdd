package codesquad.web;


import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    Question question;
    Question updateQuestion;

    @Before
    public void setup() {

        question = new Question("questionTitle", "questionContents");
        question.writeBy(defaultUser());
        questionRepository.save(question);

        updateQuestion = new Question("Update questionTitle", "Update questionContents");
        question.writeBy(defaultUser());
    }

    @Test
    public void create() {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        HttpEntity<MultiValueMap<String,Object>> request = builder
                .addParameter("title", question.getTitle())
                .addParameter("contents", question.getContents())
                .bulid();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void create_invalid_User(){
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        HttpEntity<MultiValueMap<String,Object>> request = builder
                .addParameter("title", question.getTitle())
                .addParameter("contents", question.getContents())
                .bulid();

        ResponseEntity<String> response = basicAuthTemplate(User.GUEST_USER).postForEntity("/questions", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void show(){
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        ResponseEntity<String> response = template().getForEntity(question.generateUrl(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void update(){
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        HttpEntity<MultiValueMap<String,Object>> request = builder
                .addParameter("_method", "put")
                .addParameter("title", updateQuestion.getTitle())
                .addParameter("contents", updateQuestion.getContents())
                .bulid();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity(question.generateUrl(), request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(question.generateUrl());

    }

    @Test
    public void update_invalid_user(){

    }

}