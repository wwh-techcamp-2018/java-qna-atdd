package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.UnexpectedException;
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

    private Optional<Question> findById(long id) {
        return questionRepository.findByIdAndDeletedFalse(id);
    }

    public Question findQuestionById(long id) {
        return findById(id).orElseThrow(()-> new UnexpectedException("question이 없습니다."));
    }

    public Question findQuestionByIdAndUser(long id, User loginUser) {
        return findById(id)
                .filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
       return findQuestionById(id).update(loginUser, updatedQuestion);
    }

    @Transactional
    public void delete(User loginUser, long questionId) throws CannotDeleteException {
        findQuestionById(questionId).delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        return findById(questionId)
                .orElseThrow(() -> new UnexpectedException("질문이 없어요"))
                .addAnswer(new Answer(loginUser, contents));
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        return answerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CannotDeleteException("답변을 지울 수 없어요!"))
                .delete(loginUser);
    }
}
