package codesquad.service;

import codesquad.domain.*;
import codesquad.exception.UnAuthorizedException;
import codesquad.web.UserAcceptanceTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);


    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void findByIdAndUserTest() {
        Question question = QuestionTest.newQuestion("테스트입니다.","xpxpxpxpxpxpxp");
        question.writeBy(JAVAJIGI);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        assertThat(qnaService.findByIdAndUser(JAVAJIGI,question.getId())).isEqualTo(question);
    }

    @Test (expected = UnAuthorizedException.class)
    public void findByIdAndUserTest_Fail() {
        Question question = QuestionTest.newQuestion("테스트입니다.","xpxpxpxpxpxpxp");
        question.writeBy(JAVAJIGI);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        assertThat(qnaService.findByIdAndUser(SANJIGI,question.getId())).isEqualTo(question);
    }

    @Test
    public void findAnswerByIdAndUserTest() {
        Answer answer = AnswerTest.newAnswer("test answer");
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        assertThat(qnaService.findAnswerByIdAndUser(JAVAJIGI,answer.getId())).isEqualTo(answer);
    }

    @Test (expected = UnAuthorizedException.class)
    public void findAnswerByIdAndUserTest_Fail() {
        Answer answer = AnswerTest.newAnswer("test answer");
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        assertThat(qnaService.findAnswerByIdAndUser(SANJIGI,answer.getId())).isEqualTo(answer);
    }
}
