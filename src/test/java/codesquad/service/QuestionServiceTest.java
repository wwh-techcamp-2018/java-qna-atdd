package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.assertj.core.api.Java6Assertions;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;
    private UserRepository userRepository;

    @InjectMocks
    private QnaService qnaService;
    private UserService userService;

    @Test
    public void create_성공() {

//        Question questionMock = mock(Question.class);
//
//        when(questionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(questionMock));
        Question question = new Question("테스트", "테스트질문1");
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
//        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        assertThat(qnaService.create(user, question).getId()).isNotNull();
    }
}
