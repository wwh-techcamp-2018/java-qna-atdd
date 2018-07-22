package codesquad;

public class UnAuthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "권한이 없습니다.";

    public UnAuthorizedException() {
        super(DEFAULT_MESSAGE);
    }

    public UnAuthorizedException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnAuthorizedException(String message) {
        super(message);
    }

    public UnAuthorizedException(Throwable cause) {
        super(cause);
    }
}
