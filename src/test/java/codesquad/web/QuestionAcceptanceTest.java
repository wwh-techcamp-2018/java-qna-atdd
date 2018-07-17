package codesquad.web;

import codesquad.CustomException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.HtmlUtils;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void formBuilderSet() {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void read_목록_home(){
        Question question = defaultQuestion();
        ResponseEntity<String> responseEntity = template().getForEntity("/", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().contains(question.getTitle())).isTrue();
    }

    @Test
    public void read_상세보기_성공(){
        Question question = defaultQuestion();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //sassertThat(questionRepository.findById(questionId)).isPresent();

        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d",question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug(response.getHeaders().toString());
        assertThat(response.getBody().contains(question.getContents())).isTrue();
    }

    @Test
    public void read_상세보기_실패(){
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d",3), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        log.debug("***** {}",response.getHeaders().toString());
        log.debug(HtmlUtils.htmlEscapeHex("Doesn't Exist Question", "UTF-8"));
        assertThat(response.getBody().contains(HtmlUtils.htmlEscapeHex("Doesn't Exist Question", "UTF-8"))).isTrue();
    }

    @Test
    public void create_폼_요청_실패(){
        ResponseEntity<String> responseEntity = template().getForEntity("/questions/form",String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void create_폼_요청_성공(){
        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).getForEntity("/questions/form",String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug(responseEntity.getBody());
    }

    @Test
    public void create_question_성공(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity httpEntity = htmlFormDataBuilder.addParameter("title", "제목11111")
                            .addParameter("contents", "내용111111")
                            .build();


        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions", httpEntity, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug(responseEntity.getHeaders().toString());
        assertThat(responseEntity.getHeaders().getLocation().getPath().equals("/")).isTrue();
    }

    @Test
    public void list(){
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("***** {}",response.getHeaders().toString());

        assertThat(response.getBody()).contains(defaultQuestion().getTitle());
    }
    @Test
    public void create_not_valid(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity httpEntity = htmlFormDataBuilder.addParameter("title", "제목")
                .addParameter("contents", "내용")
                .build();

        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions", httpEntity, String.class);

        log.debug("Header : {} ", responseEntity.getHeaders().toString());
        log.debug("Body : {} ",responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
//    @Test
//    public void update(){
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity
//        //sassertThat(questionRepository.findById(questionId)).isPresent();
//
//        ResponseEntity<String> response = template().getForEntity("/", String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        log.debug("***** {}",response.getHeaders().toString());
//
//        assertThat(response.getBody()).contains(defaultQuestion().getTitle());
//    }
}