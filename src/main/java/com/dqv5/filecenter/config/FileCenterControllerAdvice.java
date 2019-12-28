package com.dqv5.filecenter.config;


import com.dqv5.filecenter.exception.MultpartFileIsNullException;
import com.dqv5.filecenter.exception.NoSuchFileException;
import com.dqv5.filecenter.pojo.CommonReturn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author duq
 */
@RestControllerAdvice
@Slf4j
public class FileCenterControllerAdvice {
    @ExceptionHandler(value = NoSuchFileException.class)
    public ResponseEntity<?> noSuchFileExceptionHandler(NoSuchFileException e) {
        log.error("id={}的文件不存在", e.getFileId(), e);
        return CommonReturn.build(HttpStatus.BAD_REQUEST, "id=" + e.getFileId() + "的文件不存在", e);
    }

    @ExceptionHandler(value = MultpartFileIsNullException.class)
    public ResponseEntity<?> multpartFileIsNullExceptionHandler(MultpartFileIsNullException e) {
        log.error("接受到的上传文件为null,异常信息->{}", e.getMessage());
        return CommonReturn.build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMsg(), e);
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<?> multpartFileIsNullExceptionHandler(MaxUploadSizeExceededException e) {
        log.error("上传文件大小超过限制,异常信息->{}", e.getMessage());
        return CommonReturn.build(HttpStatus.INTERNAL_SERVER_ERROR, "上传文件大小超过限制: 1MB", e);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> exceptionHandler(Exception ex) {
        log.error("文件服务出现异常,异常信息:", ex);
        return CommonReturn.build(HttpStatus.INTERNAL_SERVER_ERROR, "程序异常", ex);
    }
}
