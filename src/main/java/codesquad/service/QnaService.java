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

    public Optional<Question> findQuestionById(long id, User writer) {
        return questionRepository.findByIdAndDeletedFalse(id).filter(question -> question.isOwner(writer));
    }

    public Optional<Question> findQuestionById(long id) {
        return questionRepository.findByIdAndDeletedFalse(id);
    }

    public Optional<Answer> findAnswerById(long id, User writer) {
        return answerRepository.findByIdAndDeletedFalse(id).filter(answer -> answer.isOwner(writer));
    }

    public Optional<Answer> findAnswerById(long id) {
        return answerRepository.findByIdAndDeletedFalse(id);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question question = findQuestionById(id).orElseThrow(EntityNotFoundException::new);
        question.update(updatedQuestion, loginUser);
        return question;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long id) throws CannotDeleteException {
        Question question = findQuestionById(id).orElseThrow(EntityNotFoundException::new);
        question.delete(loginUser);
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findQuestionById(questionId).orElseThrow(EntityNotFoundException::new);
        Answer newAnswer = new Answer(loginUser, contents);
        newAnswer.setQuestion(question);
        question.addAnswer(newAnswer);
        return answerRepository.save(newAnswer);
    }

    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = findAnswerById(id).orElseThrow(EntityNotFoundException::new);
        answer.delete(loginUser);
    }

    public List<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

}
