package codesquad.validate;

import java.util.HashMap;
import java.util.Map;

public class RestResponse extends RestStatus {
    private Map<String, Object> result;

    public RestResponse() {
        super(true);
        result = new HashMap<>();
    }

    public RestResponse addAttribute(String key, Object value) {
        result.put(key, value);
        return this;
    }

    public Map<String, Object> getResult() {
        return result;
    }
}
