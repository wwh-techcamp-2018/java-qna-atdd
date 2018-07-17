package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.*;
import codesquad.security.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.security.acl.NotOwnerException;
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
    public Question update(User loginUser, long qId, Question updatedQuestion) throws NotOwnerException {
        Question oldQuestion = questionRepository.findById(qId).filter(s -> s.isOwner(loginUser)).orElseThrow(NotOwnerException::new);
        oldQuestion.setContents(updatedQuestion.getContents());
        oldQuestion.setTitle(updatedQuestion.getTitle());
        return oldQuestion;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question q = questionRepository.findById(questionId).filter(s ->  s.isOwner(loginUser)).orElseThrow(CannotDeleteException::new);

        if(!q.getAnswers().stream().filter(s -> !s.isOwner(loginUser)).allMatch(Answer::isDeleted)) {
            throw new CannotDeleteException();
        }

        q.setDeleted(true);
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
        Question q = questionRepository.findById(questionId).orElseThrow(EntityNotFoundException::new);
        q.addAnswer(answer);
        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = answerRepository.findById(id).filter(s -> s.isOwner(loginUser)).orElseThrow(CannotDeleteException::new);
        answer.setDeleted(true);
        return answer;
    }
}
