package support.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

public class HttpRequestGenerator {

    public static HttpEntity<MultiValueMap<String, Object>> fetchRequest(MultiValueMap<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<MultiValueMap<String, Object>>(params, headers);
    }
}
