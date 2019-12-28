package com.dqv5.filecenter.support;

import com.dqv5.filecenter.enums.FileStoreType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author duq
 * @date 2019/12/28
 */
@Component
@Profile("ftp")
public class FtpFileHandler implements IntegrationFileHandler {

    @Value("${filecenter.ftp.host}")
    private String host;
    @Value("${filecenter.ftp.username}")
    private String username;
    @Value("${filecenter.ftp.password}")
    private String password;
    @Value("${filecenter.ftp.rootPath}")
    private String rootPath;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public String upload(byte[] data, String fileName) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ByteArrayInputStream fis = null;
        String newName = UUID.randomUUID().toString().replace("-", "");
        int index = fileName.lastIndexOf(".");
        if (index > -1) {
            newName += fileName.substring(index);
        }
        String subDir = dateTimeFormatter.format(LocalDate.now());
        String lj = rootPath + subDir;
        try {
            ftpClient.connect(host);
            boolean login = ftpClient.login(username, password);
            boolean b3 = ftpClient.makeDirectory(rootPath);
            fis = new ByteArrayInputStream(data);
            boolean b = ftpClient.makeDirectory(lj);
            boolean b1 = ftpClient.changeWorkingDirectory(lj);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("utf-8");
            ftpClient.enterLocalPassiveMode();
            boolean b4 = ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            boolean b2 = ftpClient.storeFile(newName, fis);
            System.out.println(b2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            fis.close();
            ftpClient.disconnect();
        }
        return lj + "/" + newName;
    }

    @Override
    public byte[] download(String storeInfo) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ByteArrayOutputStream fos = null;
        byte[] data = null;
        try {
            ftpClient.connect(host);
            ftpClient.login(username, password);
            fos = new ByteArrayOutputStream();
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("utf-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            boolean b1 = ftpClient.retrieveFile(storeInfo, fos);
            data = fos.toByteArray();
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            fos.close();
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        return data;
    }

    @Override
    public boolean support(String storeType) {
        return FileStoreType.Ftp.getValue().equals(storeType);
    }
}
