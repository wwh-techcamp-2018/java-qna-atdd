package codesquad.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class HtmlFormDataBuilder {

    private HttpHeaders headers;
    private MultiValueMap<String, Object> params;

    public static final String METHOD_PUT = "put";
    public static final String METHOD_DELETE = "delete";
    public static final String METHOD_POST = "";

    public HtmlFormDataBuilder(HttpHeaders headers){
        this.headers = headers;
        params = new LinkedMultiValueMap<>();
    }


    public HtmlFormDataBuilder addParameter(String key, Object value){
        params.add(key,value);
        return this;
    }

    public HttpEntity<MultiValueMap<String, Object>> bulid(){
        return new HttpEntity<>(params,headers);
    }

    public static HtmlFormDataBuilder urlEncodedForm(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HtmlFormDataBuilder(headers);
    }

    public HtmlFormDataBuilder addParameters(Map params) {
        Iterator itr = params.keySet().iterator();
        String key;
        while(itr.hasNext()){
            key = (String)itr.next();
            addParameter(key, params.get(key));
        }

        return this;
    }

    public HtmlFormDataBuilder method(String method) {
        if(!method.isEmpty())
            addParameter("_method", method);
        return this;
    }
}
