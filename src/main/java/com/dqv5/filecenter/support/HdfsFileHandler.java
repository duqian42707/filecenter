package com.dqv5.filecenter.support;

import com.dqv5.filecenter.enums.FileStoreType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


/**
 * 文件保存到HDFS
 *
 * @author duqian
 * @date 2019-08-20
 */
@Component
@Profile("hdfs")
@Slf4j
public class HdfsFileHandler extends AbstractFileHandler {

    private FileStoreType fileStoreType = FileStoreType.hdfs;

    @Value("${filecenter.hdfs.url}")
    private String hdfsUrl;

    @Value("${filecenter.hdfs.rootPath:/filesharesystem/}")
    private String rootPath;

    private FileSystem fs;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public FileStoreType getFileStoreType() {
        return this.fileStoreType;
    }

    /**
     * @param data     文件byte数组
     * @param fileName 文件名
     * @return
     */
    @Override
    public String upload(byte[] data, String fileName) throws IOException {
        initInstance();
        String newName = UUID.randomUUID().toString().replace("-", "");
        int index = fileName.lastIndexOf(".");
        if (index > -1) {
            newName += fileName.substring(index);
        }
        String subDir = dateTimeFormatter.format(LocalDate.now());
        String filePath = rootPath + subDir + "/" + newName;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        DataOutputStream outputStream = fs.create(new Path(filePath));
        IOUtils.copy(inputStream, outputStream);
        outputStream.close();
        inputStream.close();
        return filePath;
    }

    /**
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    @Override
    public byte[] download(String filePath) throws IOException {
        initInstance();
        DataInputStream inputStream = fs.open(new Path(filePath));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, outputStream);
        byte[] data = outputStream.toByteArray();
        outputStream.close();
        inputStream.close();
        return data;
    }


    private void initInstance() {
        if (fs == null) {
            try {
                Configuration conf = new Configuration(true);
                conf.set("fs.defaultFS", hdfsUrl);
                fs = FileSystem.get(conf);
                log.info("初始化HDFS FileSystem对象,url:{}", hdfsUrl);
            } catch (IOException e) {
                log.error("初始化HDFS异常", e);
            }
        }
    }
}
