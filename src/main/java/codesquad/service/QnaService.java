package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
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
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = findById(id).orElseThrow(EntityNotFoundException::new);
        original.update(loginUser, updatedQuestion);
        return original;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question original = findById(questionId).orElseThrow(EntityNotFoundException::new);
        original.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        Question question = findById(questionId).orElseThrow(EntityNotFoundException::new);
        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);
        questionRepository.save(question);
        return answerRepository.save(answer);
    }

    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        // TODO 답변 삭제 기능 구현
        Answer answer = answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        answer.delete(loginUser);
        return answer;
    }
}
