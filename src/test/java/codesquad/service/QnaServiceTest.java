package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    private static final String ANSWER_CONTENTS = "I am answer";

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private Question question;

    private Answer answer;

    @Before
    public void setUp() throws Exception {
        question = QuestionTest.question;
        answer = new Answer(UserTest.JAVAJIGI, ANSWER_CONTENTS);
        when(questionRepository.findByIdAndDeletedFalse(question.getId())).thenReturn(Optional.of(question));
        when(answerRepository.save(answer)).thenReturn(answer);
        when(answerRepository.findByIdAndDeletedFalse(answer.getId())).thenReturn(Optional.of(answer));
    }

    @Test
    public void update_success() throws Exception {
        Question updateQuestion = qnaService.update(UserTest.JAVAJIGI, question.getId(), QuestionTest.updateQuestion);
        assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_failed_when_user_not_writer() throws Exception {
        qnaService.update(UserTest.SANJIGI, question.getId(), QuestionTest.updateQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_failed_when_user_guest() throws Exception {
        qnaService.update(User.GUEST_USER, question.getId(), QuestionTest.updateQuestion);
    }

    @Test
    public void delete_success() throws Exception {
        qnaService.deleteQuestion(UserTest.JAVAJIGI, question.getId());
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_failed_when_user_not_writer() throws Exception {
        qnaService.deleteQuestion(UserTest.SANJIGI, question.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_failed_when_user_guest() throws Exception {
        qnaService.deleteQuestion(User.GUEST_USER, question.getId());
    }

    @Test
    public void addAnswer() {
        Answer createdAnswer = qnaService.addAnswer(UserTest.JAVAJIGI, question.getId(), ANSWER_CONTENTS);
        assertThat(createdAnswer.getContents()).isEqualTo(ANSWER_CONTENTS);
    }

    @Test
    public void deleteAnswer_success() throws CannotDeleteException {
        qnaService.deleteAnswer(UserTest.JAVAJIGI, answer.getId());
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAnswer_failed_when_user_not_writer() throws CannotDeleteException {
        qnaService.deleteAnswer(UserTest.SANJIGI, answer.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAnswer_failed_when_user_no_login() throws CannotDeleteException {
        qnaService.deleteAnswer(User.GUEST_USER, answer.getId());
    }

}
