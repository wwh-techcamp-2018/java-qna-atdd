package codesquad.web;


import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    private Question question;
    private Question updateQuestion;

    private HtmlFormDataBuilder builder;
    private Map<String,Object> params;

    @Before
    public void setup() {

        question = new Question("questionTitle", "questionContents");
        question.writeBy(defaultUser());
        questionRepository.save(question);

        updateQuestion = new Question("Update questionTitle", "Update questionContents");
        question.writeBy(defaultUser());

        builder = HtmlFormDataBuilder.urlEncodedForm();
        params = new HashMap<>();
    }

    @After
    public void tearDown(){
        questionRepository.delete(question);
    }

    @Test
    public void create() {
        params.put("title", question.getTitle());
        params.put("contents", question.getContents());
        ResponseEntity<String> response = basicAuthPostRequest("/questions"
                , defaultUser()
                , params
                , HtmlFormDataBuilder.METHOD_POST);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void create_invalid_User(){
        params.put("title", question.getTitle());
        params.put("contents", question.getContents());
        ResponseEntity<String> response = basicAuthPostRequest("/questions"
                , User.GUEST_USER
                , params
                , HtmlFormDataBuilder.METHOD_POST);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void show(){
        ResponseEntity<String> response = template().getForEntity(question.generateUrl(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void update(){
        params.put("title", updateQuestion.getTitle());
        params.put("contents", updateQuestion.getContents());
        ResponseEntity<String> response = basicAuthPostRequest(question.generateUrl()
                , defaultUser()
                , params
                , HtmlFormDataBuilder.METHOD_PUT);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(question.generateUrl());

    }

    @Test
    public void update_invalid_user(){
        params.put("title", updateQuestion.getTitle());
        params.put("contents", updateQuestion.getContents());
        ResponseEntity<String> response = basicAuthPostRequest(question.generateUrl()
                , User.GUEST_USER
                , params
                , HtmlFormDataBuilder.METHOD_PUT);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete(){
        ResponseEntity<String> response = basicAuthPostRequest(question.generateUrl()
                , defaultUser()
                , params
                , HtmlFormDataBuilder.METHOD_DELETE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void delete_invalid_user(){
        ResponseEntity<String> response = basicAuthPostRequest(question.generateUrl()
                , User.GUEST_USER
                , params
                , HtmlFormDataBuilder.METHOD_DELETE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> basicAuthPostRequest(String url, User user, Map params, String method){
        HttpEntity<MultiValueMap<String,Object>> request = makeRequest(method, params);
        return basicAuthTemplate(user)
                .postForEntity(url, request, String.class);
    }

    private HttpEntity<MultiValueMap<String,Object>> makeRequest(String method, Map params){
        return builder
                .method(method)
                .addParameters(params)
                .bulid();
    }

}