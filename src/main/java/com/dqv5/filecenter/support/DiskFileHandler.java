package com.dqv5.filecenter.support;

import com.dqv5.filecenter.enums.FileStoreType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件保存到磁盘
 *
 * @author duqian
 * @date 2019-08-21
 */
@Component
@Profile("disk")
@Slf4j
public class DiskFileHandler implements IntegrationFileHandler {

    @Value("${filecenter.disk.rootPath:/data/tempFile/}")
    private String rootPath;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * @param data     文件byte数组
     * @param fileName 文件名
     * @return 文件路径
     */
    @Override
    public String upload(byte[] data, String fileName) {
        if (!rootPath.endsWith("/")) {
            rootPath += "/";
        }
        String newName = UUID.randomUUID().toString().replace("-", "");
        int index = fileName.lastIndexOf(".");
        if (index > -1) {
            newName += fileName.substring(index);
        }
        String subDir = dateTimeFormatter.format(LocalDate.now());
        String filePath = rootPath + subDir + "/" + newName;
        File file = new File(filePath);
        try {
            FileUtils.writeByteArrayToFile(file, data);
        } catch (IOException e) {
            log.error("保存文件到磁盘异常", e);
        }
        return filePath;
    }

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @return 文件byte数组
     * @throws IOException
     */
    @Override
    public byte[] download(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new NoSuchFileException("No such file: " + filePath);
        }
        return FileUtils.readFileToByteArray(file);
    }

    @Override
    public boolean support(String storeType) {
        return FileStoreType.Disk.getValue().equals(storeType);
    }
}
