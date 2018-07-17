package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    public static Question newQuestion(Long id) {
        return QuestionTest.newQuestion(id, "default_title", "default_content");
    }

    public static Question newQuestion(String title) {
        return QuestionTest.newQuestion(0L, title, "default_content");
    }

    public static Question newQuestion(Long id, String title, String contents) {
        Question question = new Question(title, contents);
        question.setId(id);
        question.writeBy(UserTest.TEST_USER);
        return question;
    }

    public static List<Question> questionList() {
        return Arrays.asList(QuestionTest.newQuestion(1L),
                QuestionTest.newQuestion(2L, "제목2", "내용2"));
    }

    @Test
    public void update() {
        String updatedTitle = "updated_title";
        String updatedContents = "updated_contents";
        Question question = newQuestion(1L);
        Question updated = newQuestion(1L, updatedTitle, updatedContents);
        question.update(UserTest.TEST_USER, updated);
        assertThat(question.getTitle()).isEqualTo(updatedTitle);
        assertThat(question.getContents()).isEqualTo(updatedContents);
    }


    @Test (expected = UnAuthorizedException.class)
    public void updateFail() {
        String updatedTitle = "updated_title";
        String updatedContents = "updated_contents";
        Question question = newQuestion(1L);
        Question updated = newQuestion(1L, updatedTitle, updatedContents);
        question.update(UserTest.newUser(100L), updated);
    }

    @Test
    public void delete() throws CannotDeleteException {
        Question question = newQuestion(1L);
        question.delete(UserTest.TEST_USER);
        assertThat(question.isDeleted()).isTrue();
    }

    @Test (expected = CannotDeleteException.class)
    public void deleteFail() throws CannotDeleteException {
        Question question = newQuestion(1L);
        question.delete(UserTest.newUser(0L));
    }
}
