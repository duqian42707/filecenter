package com.dqv5.filecenter.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * @author duq
 * @date 2019/12/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NoSuchFileException extends RuntimeException {
    @NonNull
    private String fileId;
}
