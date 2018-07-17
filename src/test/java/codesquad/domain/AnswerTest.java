package codesquad.domain;

import static codesquad.domain.UserTest.JAVAJIGI;

public class AnswerTest {


    public static Answer newAnswer(String contents) {
        return new Answer(JAVAJIGI,contents);
    }


}
