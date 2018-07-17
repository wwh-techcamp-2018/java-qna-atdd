package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {

    public static final User other = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    public static final User writer = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final Question question = new Question(1, writer, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다. Play 프레임워크는 정말 한 순간 잠시 눈에 뜨이다가 사라져 버렸다. RoR과 Play 기반으로 개발을 해보면 정말 생산성이 높으며, 웹 프로그래밍이 재미있기까지 하다. Spring MVC + JPA(Hibernate) 기반으로 진행하면 설정할 부분도 많고, 기본으로 지원하지 않는 기능도 많아 RoR과 Play에서 기본적으로 지원하는 기능을 서비스하려면 추가적인 개발이 필요하다.");

    @Test
    public void update_owner() throws Exception {
        User loginUser = writer;
        Question target = new Question("(updated title)", "(updated contents)");
        question.update(loginUser, target);
        assertThat(question.getTitle()).isEqualTo(target.getTitle());
        assertThat(question.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        User loginUser = other;
        Question target = new Question("(updated title)", "(updated contents)");
        question.update(loginUser, target);
    }

    @Test
    public void delete_owner() throws Exception {
        User loginUser = writer;
        assertThat(question.isDeleted()).isFalse();
        question.delete(loginUser);
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws Exception {
        User loginUser = other;
        question.delete(loginUser);
    }
}
