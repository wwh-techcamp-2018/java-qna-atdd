package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    public static final Question question = new Question(1L, UserTest.JAVAJIGI, "Test Title", "Test contents");
    public static final Question updateQuestion = new Question("Update Title", "Update contents");

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        question.update(updateQuestion, UserTest.SANJIGI);
    }

    @Test
    public void update_as_owner() {
        question.update(updateQuestion, UserTest.JAVAJIGI);
        assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_as_guest() {
        question.update(updateQuestion, User.GUEST_USER);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws CannotDeleteException {
        question.delete(UserTest.SANJIGI);
    }

    @Test
    public void delete_as_owner() throws CannotDeleteException {
        question.delete(UserTest.JAVAJIGI);
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_as_guest() throws CannotDeleteException {
        question.delete(User.GUEST_USER);
    }
}