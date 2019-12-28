package com.dqv5.filecenter.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author duqian
 * @date 2019-08-21
 */
@Data
@Builder
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String filename;
    private long length;
    private Date uploadDate;
    private String md5;
    private String type;
    private String storeType;
    private String storeInfo;

    @JsonIgnore
    private byte[] data;
}
