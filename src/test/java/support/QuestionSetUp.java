package support;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.QuestionTest;
import codesquad.domain.User;

import java.util.List;

public class QuestionSetUp {
    public static List<Question> setUp(QuestionRepository questionRepository, User writer) {
        questionRepository.deleteAll();
        List<Question> questionList = QuestionTest.questionList();
        for (Question question : questionList) {
            question.writeBy(writer);
        }

        questionRepository.saveAll(questionList);
        return questionRepository.findAll();
    }
}
