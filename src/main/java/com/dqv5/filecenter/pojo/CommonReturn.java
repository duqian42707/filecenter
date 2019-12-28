package com.dqv5.filecenter.pojo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;

public class CommonReturn {
    public CommonReturn() {
    }

    public static ResponseEntity<CommonReturnEntity> build(boolean isSuccess, String message) {
        return isSuccess ? ((BodyBuilder)ResponseEntity.ok().headers(Utf8ContentTypeHeader.build())).body(CommonReturnEntity.builder().message(message).build()) : ((BodyBuilder)ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(Utf8ContentTypeHeader.build())).body(CommonReturnEntity.builder().message(message).build());
    }

    public static ResponseEntity<CommonReturnEntity> build(HttpStatus status, String message) {
        return ((BodyBuilder)ResponseEntity.status(status).headers(Utf8ContentTypeHeader.build())).body(CommonReturnEntity.builder().message(message).build());
    }

    public static ResponseEntity<CommonReturnEntity> build(boolean isSuccess, String message, Object data) {
        return isSuccess ? ((BodyBuilder)ResponseEntity.ok().headers(Utf8ContentTypeHeader.build())).body(CommonReturnEntity.builder().message(message).data(data).build()) : ((BodyBuilder)ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(Utf8ContentTypeHeader.build())).body(CommonReturnEntity.builder().message(message).data(data).build());
    }

    public static ResponseEntity<CommonReturnEntity> build(HttpStatus status, String message, Object data) {
        return ((BodyBuilder)ResponseEntity.status(status).headers(Utf8ContentTypeHeader.build())).body(CommonReturnEntity.builder().message(message).data(data).build());
    }

    public static ResponseEntity<CommonReturnEntity> build(HttpStatus status, String message, Object data, Exception ex) {
        return ((BodyBuilder)ResponseEntity.status(status).headers(Utf8ContentTypeHeader.build())).body(CommonReturnEntity.builder().message(message).data(data).errorMessage(ex.getMessage()).build());
    }

    public static ResponseEntity<CommonReturnEntity> build(HttpStatus status, String message, Exception ex) {
        return ((BodyBuilder)ResponseEntity.status(status).headers(Utf8ContentTypeHeader.build())).body(CommonReturnEntity.builder().message(message).errorMessage(ex.getMessage()).build());
    }
}
