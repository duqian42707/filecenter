package com.dqv5.filecenter.service;

import com.alibaba.fastjson.JSONObject;
import com.dqv5.filecenter.entity.FilecenterInfo;
import com.dqv5.filecenter.pojo.FileInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author duqian
 * @date 2019-08-22
 */
public interface FileService {

    Page<FilecenterInfo> queryListForPage(Specification<FilecenterInfo> specification, Pageable pageable);

    long totalSize();

    FileInfo upload(MultipartFile file) throws IOException;

    FileInfo getFileInfo(String id);

    /**
     * 转移远程存储的文件到当前存储方式
     *
     * @throws IOException something error
     */
    void transferRemoteFile() throws IOException;


    FileInfo download(String id) throws IOException;

    FileInfo downloadZip(String id) throws IOException;


    void delete(String id);


}
