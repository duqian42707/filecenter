package com.dqv5.filecenter.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * @author duq
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MultpartFileIsNullException extends RuntimeException {
    @NonNull
    private String msg;
}
