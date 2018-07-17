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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    private List<Question> questions;
    private Question question;

    @InjectMocks
    private QnaService questionService;

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerRepository answerRepository;

    @Before
    public void setUp() throws Exception {
        questions = QuestionTest.questionList();
        question = questions.get(0);
    }

    @Test
    public void create() {
        User user = UserTest.newUser(1L);
        Question question = QuestionTest.newQuestion(1L);
        when(questionRepository.save(question)).thenReturn(question);

        assertThat(questionService.create(user, question).getWriter().getId()).isEqualTo(user.getId());
    }

    @Test
    public void update() {
        Question updateQuestion = QuestionTest.newQuestion(
                question.getId(),
                question.getTitle() + "1",
                question.getContents() + "1"
        );

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        Question updated = questionService.update(
                UserTest.TEST_USER,
                question.getId(),
                updateQuestion
            );

        assertThat(updated.getTitle()).isEqualTo(updateQuestion.getTitle());
        assertThat(updated.getContents()).isEqualTo(updateQuestion.getContents());
    }


    @Test (expected = UnAuthorizedException.class)
    public void updateFail() {
        Question updateQuestion = QuestionTest.newQuestion(
                question.getId(),
                question.getTitle() + "1",
                question.getContents() + "1"
        );

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        questionService.update(
                UserTest.newUser(0L),
                question.getId(),
                updateQuestion
        );
    }

    @Test
    public void deleteQuestion() throws CannotDeleteException {
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        questionService.deleteQuestion(UserTest.TEST_USER, question.getId());
        assertThat(question.isDeleted()).isTrue();
    }

    @Test (expected = CannotDeleteException.class)
    public void deleteQuestionFail() throws CannotDeleteException {
        questionService.deleteQuestion(UserTest.newUser(0L), question.getId());
    }

    @Test
    public void findAll() {
        when(questionRepository.findByDeleted(false)).thenReturn(questions);
        Iterable<Question> found = questionService.findAll();
        assertThat(found).hasSameElementsAs(questions);
    }

    @Test
    public void addAnswer() {
        Answer answer = new Answer(0L, UserTest.TEST_USER, question, "contents");

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        when(answerRepository.save(answer)).thenReturn(answer);

        Answer newAnswer = questionService.addAnswer(UserTest.TEST_USER, question.getId(), "contents");
        assertThat(newAnswer).isEqualTo(answer);
    }

    @Test
    public void deleteAnswer() {
    }
}