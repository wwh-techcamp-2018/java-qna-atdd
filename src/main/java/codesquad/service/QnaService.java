package codesquad.service;

import codesquad.exception.CannotDeleteException;
import codesquad.domain.*;
import codesquad.exception.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.NoSuchElementException;
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

    public Question findById(long id) {
        return questionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("게시물을 찾을 수 없습니다."));
    }

    public Answer findByAnswerId(long answerId) {
        return answerRepository.findById(answerId).orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다."));
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question question = questionRepository.findById(id).filter(q -> q.isOwner(loginUser)).orElseThrow(() -> new UnAuthorizedException("자신의 질문만 수정이 가능합니다."));
        question.update(updatedQuestion);
        return question;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long id) throws CannotDeleteException {
        Question question = questionRepository.findById(id).filter(q -> q.isOwner(loginUser)).orElseThrow(() -> new CannotDeleteException("자신의 질문만 삭제가 가능합니다."));
        question.delete();
        return question;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = questionRepository.findById(questionId).get();
        Answer answer = new Answer(loginUser, contents);
        answer.toQuestion(question);
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = answerRepository.findById(id).filter(a -> a.isOwner(loginUser)).orElseThrow(() -> new CannotDeleteException("자신의 답변만 삭제할 수 있습니다."));
        answer.delete();
        return answer;
    }
}
