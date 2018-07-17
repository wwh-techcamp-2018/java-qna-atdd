package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.ResourceNotFoundException;
import codesquad.UnAuthorizedException;
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

    public Question findById(long id, User user) {
        return questionRepository.findByIdAndWriter(id, user).orElse(null);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question origin = questionRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        origin.update(loginUser, updatedQuestion);

        return origin;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId).orElseThrow(RuntimeException::new);
        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        questionRepository.delete(question);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        Question question = questionRepository.findById(questionId).orElseThrow(ResourceNotFoundException::new);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        Answer deletedAnswer = answerRepository.findById(id).filter(answer -> answer.isOwner(loginUser)).orElseThrow(UnAuthorizedException::new);
        answerRepository.delete(deletedAnswer);
        return deletedAnswer;
    }
}
