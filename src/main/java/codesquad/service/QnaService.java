package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.CustomException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.ws.http.HTTPException;
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
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }
    public Question matchWriter(long id, User loginUser){
        Question question = findById(id).orElseThrow(() -> new CustomException("Not Found"));
        if(!question.isOwner(loginUser)){
            throw new UnAuthorizedException("권한이 없습니다");
        }
        return question;
    }
    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question forUpdataQuestion = matchWriter(id, loginUser);
        return questionRepository.save(forUpdataQuestion.updateData(updatedQuestion));
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = matchWriter(questionId, loginUser);
        if(!question.isDeletable())
            throw new CannotDeleteException("글을 지울수 없네요");
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
        Question question = findById(questionId).orElseThrow(() -> new CustomException("Not Found"));
        Answer newAnswer = new Answer(loginUser, contents);
        newAnswer.toQuestion(question);
        question.addAnswer(newAnswer);
        return newAnswer;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        Answer targetAnswer = answerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Not Found"));
        if(!targetAnswer.isOwner(loginUser)){
            throw new UnAuthorizedException("권한이 없습니다");
        }


        answerRepository.deleteById(id);
        log.debug("question update? : {}", questionRepository.findById((long)1).get());

        return targetAnswer;
    }


}
