package codesquad;

public class CustomException extends RuntimeException {
    public final static String DEFAULT_MESSAGE = "페이지를 찾을 수 없습니다.";
    public CustomException(){
        super(DEFAULT_MESSAGE);
    }
    public CustomException(String message) {
        super(message);
    }
}
