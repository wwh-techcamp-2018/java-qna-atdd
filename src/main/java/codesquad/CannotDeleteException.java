package codesquad;

public class CannotDeleteException extends Exception {
    public final static String DEFAULT_MESSAGE = "삭제할 수 없습니다.";
    private static final long serialVersionUID = 1L;

    public CannotDeleteException() {
        super(DEFAULT_MESSAGE);
    }

    public CannotDeleteException(String message) {
        super(message);
    }
}