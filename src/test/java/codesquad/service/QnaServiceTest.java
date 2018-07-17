package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void create() {
        User user = new User("javajigi", "test", "javajigi_name", "javajigi@woowa.com");
        Question question = new Question("title1", "contents1");
        qnaService.create(user, question);

        verify(questionRepository).save(question);
    }

    @Test
    public void update() {
        User user = new User("javajigi", "test", "javajigi_name", "javajigi@woowa.com");
        Question oldquestion = new Question("title1_old", "contents1_old");
        oldquestion.setId(1l);
        oldquestion.writeBy(user);
        Question question = new Question("title1_updated", "contents1_updated");
        when(questionRepository.findById(1l)).thenReturn(Optional.of(oldquestion));

        Question updatedQuestion = qnaService.update(user, 1l, question);
        assertThat(updatedQuestion.getTitle()).isEqualTo("title1_updated");
        assertThat(updatedQuestion.getId()).isEqualTo(1l);
        assertThat(updatedQuestion.getWriter().getName()).isEqualTo("javajigi_name");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_match() {
        User user = new User("javajigi", "test", "javajigi_name", "javajigi@woowa.com");
        User user2 = new User("sanjigi", "test", "", "");
        Question oldquestion = new Question("title1_old", "contents1_old");
        oldquestion.setId(1l);
        oldquestion.writeBy(user);
        Question question = new Question("title1_updated", "contents1_updated");
        when(questionRepository.findById(1l)).thenReturn(Optional.of(oldquestion));
        Question updatedQuestion = qnaService.update(user2, 1l, question);
    }

    @Test
    public void deleteQuestion() {
        User user = new User("javajigi", "test", "javajigi_name", "javajigi@woowa.com");
        Question oldquestion = new Question("title1_old", "contents1_old");
        oldquestion.setId(1l);
        oldquestion.writeBy(user);
        when(questionRepository.findById(1l)).thenReturn(Optional.of(oldquestion));

        Question deletedQuestion = qnaService.deleteQuestion(user, 1l);
        assertThat(deletedQuestion.getTitle()).isEqualTo("title1_old");
        assertThat(deletedQuestion.getWriter().getName()).isEqualTo("javajigi_name");
        assertThat(deletedQuestion.isDeleted()).isEqualTo(true);

    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestion_not_match () {
        User user = new User("javajigi", "test", "javajigi_name", "javajigi@woowa.com");
        User user2 = new User("sanjigi", "test", "", "");
        Question oldquestion = new Question("title1_old", "contents1_old");
        oldquestion.setId(1l);
        oldquestion.writeBy(user);
        when(questionRepository.findById(1l)).thenReturn(Optional.of(oldquestion));
        Question deletedQuestion = qnaService.deleteQuestion(user2, 1l);
    }

    @Test
    public void addAnswer() {
        Question question = new Question("title1", "contents1");
        when(questionRepository.findById(1l)).thenReturn(Optional.of(question));
        User user = new User("javajigi", "test", "javajigi_name", "javajigi@woowa.com");
        Answer answer = qnaService.addAnswer(user, 1, "test answer1 contents");

        assertThat(answer.getWriter().getName()).isEqualTo("javajigi_name");
        assertThat(answer.getContents()).isEqualTo("test answer1 contents");
    }

    @Test
    public void deleteAnswer() {
        Answer answer = new Answer();
        answer.setContents("blahblah");
        User user = new User();
        user.setUserId("javajigi");
        user.setPassword("test");
        answer.writeBy(user);
        when(answerRepository.findById(1l)).thenReturn(Optional.of(answer));

        Answer deletedAnswer = qnaService.deleteAnswer(user, 1l);

        assertThat(deletedAnswer.isDeleted()).isTrue();
        assertThat(deletedAnswer.getContents()).isEqualTo("blahblah");
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAnswer_not_match() {
        Answer answer = new Answer();
        answer.setContents("blahblah");
        User user = new User("javajigi", "test", "","");
        User user2 = new User("sanjigi", "test", "", "");
        answer.writeBy(user2);
        when(answerRepository.findById(1l)).thenReturn(Optional.of(answer));

        Answer deletedAnswer = qnaService.deleteAnswer(user, 1l);
    }
}