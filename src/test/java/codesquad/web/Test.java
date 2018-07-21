package codesquad.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.match.ContentRequestMatchers;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Arrays;

public class Test extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    private HtmlFormDataBuilder htmlFormDataBuilder;
    private HttpEntity httpEntity;
    @Before
    public void formBuilderSet() {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    }

    @org.junit.Test
    public void create_question_test(){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        try {
            json.put("title", "2");
            json.put("contents", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpEntity <String> httpEntity = new HttpEntity <String> (json.toString(), httpHeaders);

        Object responseEntity = basicAuthTemplate(defaultUser()).postForObject("/questions", httpEntity, String.class);
    }

    @org.junit.Test
    public void create_question_test1(){
        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions/test1", httpEntity, String.class);
    }

    @org.junit.Test
    public void create_question_test2(){
        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions/test2", httpEntity, String.class);
    }
    @org.junit.Test
    public void create_question_test3(){
        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions/test3", httpEntity, String.class);
    }
    @org.junit.Test
    public void create_question_test4(){
        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions/test4", httpEntity, String.class);
    }
    @org.junit.Test
    public void create_question_test5(){
        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions/test5", httpEntity, String.class);
    }
    @org.junit.Test
    public void create_question_test6(){
        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions/test6", httpEntity, String.class);
    }
    @org.junit.Test
    public void create_question_test7(){
        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions/test7", httpEntity, String.class);
    }


}
