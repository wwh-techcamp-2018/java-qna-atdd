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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private User user;
    private User otherUser;
    private Question question;
    private Question expectedQuestion;
    private Answer answer;

    @Before
    public void setUp() throws Exception {
        user = new User("sanjigi", "password", "name", "sanjigi@slipp.net");
        user.setId(100);

        otherUser = new User("javajigi", "password", "javajigi", "javajigi@slipp.net");
        otherUser.setId(99);

        question = new Question("title", "contents");
        question.setId(1);
        question.writeBy(user);

        answer = new Answer((long) 0, user, question, "answerContents");

        when(questionRepository.save(question)).thenReturn(question);
        when(questionRepository.findByIdAndDeletedFalse((long) 1)).thenReturn(Optional.of(question));
        when(answerRepository.findByIdAndDeletedFalse(answer.getId())).thenReturn(Optional.of(answer));
    }

    @Test
    public void createQuestion() {
        Question returnedQuestion = qnaService.create(user, question);
        assertThat(returnedQuestion.getWriter()).isEqualTo(user);
    }

    @Test
    public void readQuestion() {
        assertThat(qnaService.findQuestionById((long) 1)).isEqualTo(question);
    }

    @Test
    public void updateQuestion() {
        Question updatedQuestion = new Question("updatedTitle", "updatedContents");
        Question expectedQuestion = buildExpectedQuestion(question, updatedQuestion);
        assertThat(qnaService.update(user, question.getId(), updatedQuestion)).isEqualTo(expectedQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateQuestionFail() {
        Question updatedQuestion = new Question("updatedTitle", "updatedContents");
        qnaService.update(otherUser, question.getId(), updatedQuestion);
    }

    private Question buildExpectedQuestion(Question original, Question updated) {
        Question expectedQuestion = new Question(updated.getTitle(), updated.getContents());
        expectedQuestion.setId(original.getId());
        expectedQuestion.writeBy(original.getWriter());
        return expectedQuestion;

    }

    @Test
    public void deleteQuestion() throws CannotDeleteException {
        qnaService.delete(user, question.getId());
        assertThat(question.isDeleted()).isEqualTo(true);
        assertThat(question.getAnswers().stream().allMatch(Answer::isDeleted)).isEqualTo(true);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestionFail() throws CannotDeleteException {
        qnaService.delete(otherUser, question.getId());
    }

    @Test
    public void addAnswer() {
        Answer returnedAnswer = qnaService.addAnswer(user, question.getId(), answer.getContents());
        assertThat(question.getAnswers().get(0)).isEqualTo(returnedAnswer);
        assertThat(returnedAnswer).isEqualTo(answer);
    }

    @Test
    public void deleteAnswer() throws CannotDeleteException {
        Answer returnedAnswer = qnaService.deleteAnswer(user, answer.getId());
        assertThat(returnedAnswer).isEqualTo(answer);
        assertThat(returnedAnswer.isDeleted()).isEqualTo(true);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAnswerFail() throws CannotDeleteException {
        qnaService.deleteAnswer(otherUser, answer.getId());
    }
}