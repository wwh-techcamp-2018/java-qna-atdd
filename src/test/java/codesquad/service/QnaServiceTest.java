package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
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
}