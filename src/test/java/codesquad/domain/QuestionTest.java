package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionTest {

    Question question;
    Question updateQuestion;
    User writer;

    @Before
    public void setUp() throws Exception {

        writer = new User(1, "javajigi", "1234" , "포비", "javajigi@gmail.com");

        question = new Question("questionTitle", "questionContents");
        question.writeBy(writer);

        updateQuestion = new Question("Update questionTitle", "Update questionContents");
        question.writeBy(writer);
    }

    @Test
    public void update(){
        question.update(writer, updateQuestion);

        assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_exception(){
        question.update(User.GUEST_USER, updateQuestion);
    }

    @Test
    public void delete(){
        question.delete(writer);
        assertThat(question.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_exception(){
        question.delete(User.GUEST_USER);
    }
}