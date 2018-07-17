package codesquad.domain;

import codesquad.CannotDeleteException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    private Question question;
    private User loginUser;

    @Before
    public void setUp() throws Exception {
        question = new Question("질문1", "질문1의 내용");
        loginUser = new User("whddhks", "1234", "홍종완", "tech_jwh@woowahan.com");
        question.writeBy(loginUser);
    }

    @Test
    public void update() {
        Question updatedQuestion = new Question("수정 질문", "수정 질문의 내용");
        question.update(loginUser, updatedQuestion);
        assertThat(question.getTitle()).isEqualTo(updatedQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(updatedQuestion.getContents());
    }

    @Test
    public void delete() throws CannotDeleteException {
        question.delete(loginUser);
        assertThat(question.isDeleted()).isTrue();
    }
}