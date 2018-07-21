package codesquad;

public class UnAuthenticationException extends Exception {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "로그인이 필요합니다.";

    public UnAuthenticationException() {
        super(DEFAULT_MESSAGE);
    }

    public UnAuthenticationException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnAuthenticationException(String message) {
        super(message);
    }

    public UnAuthenticationException(Throwable cause) {
        super(cause);
    }
}
