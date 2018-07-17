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

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    private Question question;

    @Before
    public void setUp() throws Exception {
        question = QuestionTest.question;
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
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

}
