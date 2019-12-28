package com.dqv5.filecenter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author duqian
 * @date 2019-08-20
 */
@Getter
@AllArgsConstructor
public enum FileStoreType {
    /**
     * 文件存储类型
     */
    Remote("0", "网络地址"), Mongo("1", "Mongo"), HDFS("2", "HDFS"), Disk("3", "本地磁盘"), Ftp("4", "ftp");

    private String value;
    private String name;

}
