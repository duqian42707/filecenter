package com.dqv5.filecenter.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author duq
 * @date 2019/12/28
 */
@Data
@Builder
public class CommonReturnEntity<T> {
    private String message;
    private T data;
    private String errorMessage;
}
