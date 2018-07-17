package codesquad.service;

import codesquad.exception.CannotDeleteException;
import codesquad.domain.*;
import codesquad.exception.UnAuthorizedException;
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

    public Question findByIdAndUser(User loginUser, Long id){
        return questionRepository.findById(id).filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    public Answer findAnswerByIdAndUser(User loginUser, Long id){
        return answerRepository.findById(id).filter(answer -> answer.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public Question update(User loginUser, long questionId, Question updatedQuestion) {
        Question original = findById(questionId).get();
        original.update(loginUser,updatedQuestion);
        return original;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question target = findByIdAndUser(loginUser,questionId);
        return  target.delete();
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        Question question = findById(questionId).get();
        answer.toQuestion(question);
        question.addAnswer(answer);

        return answerRepository.save(answer);
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        Answer answer = findAnswerByIdAndUser(loginUser, id);
        answer.delete();
        return answer;
    }
}
