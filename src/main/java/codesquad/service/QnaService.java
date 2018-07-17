package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public void update(User loginUser, long id, Question updatedQuestion) {
        // TODO: 2018. 7. 17. 존재하지 않는 질문에 대해서 예외처리가 필요합니다.
        Question question = questionRepository.findById(id).get();
        question.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        // TODO: 2018. 7. 17. 존재하지 않는 질문에 대해서 예외처리 핸들러가 필요합니다.
        Question question = questionRepository.findById(questionId).orElseThrow(CannotDeleteException::new);
        question.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO: 2018. 7. 17. 없는 질문이면요?
        Answer answer = new Answer(loginUser, contents);
        Question question = findById(questionId).get().addAnswer(answer);
        return answerRepository.save(answer);
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO: 2018. 7. 17. 없는 답변이면요?
        return answerRepository.save(answerRepository.findById(id).get().delete(loginUser));
    }
}
