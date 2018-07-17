package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    private Question origin;
    private Question updated;

    @Before
    public void setUp() throws Exception {
        origin = new Question("title1", "contents1");
        origin.writeBy(UserTest.JAVAJIGI);
        updated = new Question("newQuestion", "newContents");
    }

    @Test
    public void update_owner() throws Exception {
        origin.update(UserTest.JAVAJIGI, updated);
        assertThat(origin.getTitle()).isEqualTo(updated.getTitle());
        assertThat(origin.getContents()).isEqualTo(updated.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        origin.update(UserTest.SANJIGI, updated);
    }


}
