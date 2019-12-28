package com.dqv5.filecenter.pojo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class Utf8ContentTypeHeader {
    public Utf8ContentTypeHeader() {
    }

    public static HttpHeaders build() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;charset=UTF-8"));
        return headers;
    }
}
