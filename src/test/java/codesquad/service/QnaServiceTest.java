package codesquad.service;

import codesquad.domain.*;
import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService questionService;

    private User user;
    private User loginUser;
    private Question question;
    private Answer answer;

    @Before
    public void setUp() throws Exception {
        question = new Question("title", "contents");
        user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        question.writeBy(user);
        loginUser = new User("javajigi", "password", "1234", "javajigi@slipp.net");
        answer = new Answer(user, "answer");
    }

    @Test
    public void update_success() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Question updatedQuestion = new Question("title2", "contents2");
        assertThat(updatedQuestion, is(questionService.update(user, 1L, updatedQuestion)));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_fail_when_mismatch_user() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Question updatedQuestion = new Question("title2", "contents2");
        questionService.update(loginUser, 1L, updatedQuestion);
    }

    @Test
    public void delete_success() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        assertThat(true, is(questionService.deleteQuestion(user, 1L).isDeleted()));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_fail_when_mismatch_user() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        questionService.deleteQuestion(loginUser, 1L);
    }

    @Test
    public void create_answer_success() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Answer answer = questionService.addAnswer(loginUser, 1L, "hello");
        assertThat("hello", is(answer.getContents()));
    }

    @Test
    public void delete_answer_success() throws CannotDeleteException {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        assertThat(true, is(questionService.deleteAnswer(user, 1L).isDeleted()));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_fail_when_mismatch_user() throws CannotDeleteException {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        assertThat(true, is(questionService.deleteAnswer(loginUser, 1L).isDeleted()));
    }

}

