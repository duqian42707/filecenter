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
    remote("remote", "网络地址"), mongo("mongo", "Mongo"), hdfs("hdfs", "HDFS"), disk("disk", "本地磁盘"), ftp("ftp", "ftp");

    private String value;
    private String name;

}
