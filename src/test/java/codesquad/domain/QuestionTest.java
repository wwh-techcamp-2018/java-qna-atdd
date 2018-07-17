package codesquad.domain;

import codesquad.exception.UnAuthorizedException;
import org.junit.Test;


import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;
import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    public static final Question TARGET = newQuestion("test title update", "test contents update");
    public static Question newQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    public static Question makeQuestion() {
        Question origin = newQuestion("test title","test contents");
        origin.writeBy(JAVAJIGI);
        return origin;
    }

    @Test
    public void updateTest() {
        Question origin = makeQuestion();

        User loginUser = JAVAJIGI;

        assertThat(origin.update(loginUser, TARGET).getTitle()).isEqualTo(TARGET.getTitle());
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateTestFail() {
        Question origin = makeQuestion();

        User loginUser = SANJIGI;

        assertThat(origin.update(loginUser, TARGET).getTitle()).isEqualTo(TARGET.getTitle());
    }

    @Test
    public void deleteTest() {
        Question target = makeQuestion();
        assertThat(target.delete().isDeleted()).isTrue();
    }

    @Test
    public void isDeletableTest() {
        Question target = makeQuestion();
        target.addAnswer(AnswerTest.newAnswer("contents"));
        assertThat(target.isDeletable()).isTrue();
    }

    @Test
    public void isDeletableTestFail() {
        Question target = makeQuestion();
        target.writeBy(SANJIGI);
        target.addAnswer(AnswerTest.newAnswer("contents"));
        assertThat(target.isDeletable()).isFalse();
    }
}
