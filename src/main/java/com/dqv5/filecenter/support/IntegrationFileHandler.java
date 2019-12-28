package com.dqv5.filecenter.support;

import java.io.IOException;

/**
 * 整合文件上传通用方法
 *
 * @author duqian
 * @date 2019-08-21
 */
public interface IntegrationFileHandler {
    /**
     * 文件上传，返回文件存储信息。
     *
     * @param data     文件byte数组
     * @param fileName 文件名
     * @return 文件存储信息
     */
    String upload(byte[] data, String fileName) throws IOException;

    /**
     * 文件下载
     *
     * @param storeInfo 文件存储信息
     * @return 文件byte数组
     * @throws IOException
     */
    byte[] download(String storeInfo) throws IOException;

    /**
     * 是否支持当前存储方式
     *
     * @param storeType 存储方式
     * @return
     */
    boolean support(String storeType);
}
