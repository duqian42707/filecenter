package com.dqv5.filecenter.service.impl;

import com.dqv5.filecenter.entity.FilecenterInfo;
import com.dqv5.filecenter.enums.FileStoreType;
import com.dqv5.filecenter.exception.NoSuchFileException;
import com.dqv5.filecenter.pojo.FileInfo;
import com.dqv5.filecenter.repository.FilecenterInfoRepository;
import com.dqv5.filecenter.service.FileService;
import com.dqv5.filecenter.support.IntegrationFileHandler;
import com.dqv5.filecenter.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author duqian
 * @date 2019-08-20
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Resource
    private FilecenterInfoRepository filecenterInfoRepository;

    @Resource
    private List<IntegrationFileHandler> fileHandlers;


    /**
     * 上传时的储存方式
     *
     */
    @Value("${filecenter.uploadStoreType:1}")
    private String uploadStoreType;


    private String dir = "/opt/data/";


    @Override
    public Page<FilecenterInfo> queryListForPage(Specification<FilecenterInfo> specification, Pageable pageable) {
        return this.filecenterInfoRepository.findAll(specification, pageable);
    }

    @Override
    public long totalSize() {
        Long sum = this.filecenterInfoRepository.sumTotalSize();
        if (sum == null) {
            return 0;
        }
        return sum;
    }

    @Override
    public FileInfo upload(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        inputStream = file.getInputStream();
        IOUtils.copy(inputStream, outputStream);
        byte[] data = outputStream.toByteArray();
        outputStream.close();
        inputStream.close();
        return this.upload(data, filename);
    }


    @Override
    public FileInfo getFileInfo(String id) {
        FilecenterInfo filecenterInfo = this.filecenterInfoRepository.findById(id).orElseThrow(() -> new NoSuchFileException(id));
        return FileInfo.builder()
                .id(filecenterInfo.getId())
                .filename(filecenterInfo.getFilename())
                .length(filecenterInfo.getLength())
                .storeType(filecenterInfo.getStoreType())
                .storeInfo(filecenterInfo.getStoreInfo())
                .md5(filecenterInfo.getMd5())
                .type(filecenterInfo.getType())
                .uploadDate(filecenterInfo.getUploadDate())
                .build();
    }

    /**
     * 将远程服务器文件转移到当前存储方式
     *
     * @throws IOException
     */
    @Override
    public void transferRemoteFile() throws IOException {
        String remoteStoreType = FileStoreType.Remote.getValue();
        if (this.uploadStoreType.equals(remoteStoreType)) {
            return;
        }
        List<FilecenterInfo> all = this.filecenterInfoRepository.findByStoreType(remoteStoreType);
        for (FilecenterInfo filecenterInfo : all) {
            String originalStoreInfo = filecenterInfo.getStoreInfo();
            String fileName = filecenterInfo.getFilename();
            IntegrationFileHandler remoteFileHandler = getSupportFileHandler(remoteStoreType);
            byte[] data = null;
            try {
                data = remoteFileHandler.download(originalStoreInfo);
            } catch (Exception e) {
                log.info("转存远程文件异常：文件下载失败：" + e.getMessage());
                continue;
            }
            IntegrationFileHandler fileHandler = getSupportFileHandler(this.uploadStoreType);
            String storeInfo = fileHandler.upload(data, fileName);
            filecenterInfo.setStoreType(this.uploadStoreType);
            filecenterInfo.setStoreInfo(storeInfo);
            this.filecenterInfoRepository.save(filecenterInfo);
            log.debug("转存远程文件" + fileName + "成功");
        }
    }


    @Override
    public FileInfo download(String id) throws IOException {
        FileInfo fileInfo = this.getFileInfo(id);
        String storeType = fileInfo.getStoreType();
        IntegrationFileHandler fileHandler = getSupportFileHandler(storeType);
        String storeInfo = fileInfo.getStoreInfo();
        byte[] data = fileHandler.download(storeInfo);
        fileInfo.setData(data);
        return fileInfo;
    }

    private String getMd5(byte[] data) {
        String md5 = DigestUtils.md5DigestAsHex(data);
        int length = data.length;
        byte[] subData = new byte[Math.min(length, 10)];
        int subDataLength = subData.length;
        for (int i = 0; i < subDataLength; i++) {
            int index = Math.abs(data[i]);
            while (index >= length) {
                index = index - length;
            }
            subData[i] = data[index];
        }
        String salt = DigestUtils.md5DigestAsHex(subData) + "2851";
        md5 += salt;
        md5 = DigestUtils.md5DigestAsHex(md5.getBytes());
        return md5;
    }

    public FileInfo upload(byte[] data, String fileName) throws IOException {
        String fileType = "unknown";
        if (fileName.contains(".")) {
            fileType = fileName.substring(fileName.lastIndexOf("."));
        }
//        if (YzFileUtils.isPic(fileType)) {
//            // 处理苹果设备拍照会自动旋转的问题
//            data = ImageExifUtils.handleAppleRotate(data);
//        }
        String md5 = getMd5(data);
        FilecenterInfo filecenterInfo = new FilecenterInfo();
        List<FilecenterInfo> fileCenterInfoList = this.filecenterInfoRepository.findByMd5(md5);
        String storeInfo;
        String storeType;
        FilecenterInfo existInfo = null;
//        if (fileCenterInfoList != null && !fileCenterInfoList.isEmpty()) {
//            for (FilecenterInfo info : fileCenterInfoList) {
//                try {
//                    // 尝试下载
//                    IntegrationFileHandler fileHandler = getSupportFileHandler(info.getStoreType());
//                    String storeInfo1 = info.getStoreInfo();
//                    byte[] download = fileHandler.download(storeInfo1);
//                    if (download != null) {
//                        // 能够下载成功
//                        existInfo = info;
//                        break;
//                    }
//                } catch (Exception e) {
//                    // 下载失败
//                }
//            }
//        }
        if (existInfo != null) {
            storeType = existInfo.getStoreType();
            storeInfo = existInfo.getStoreInfo();
        } else {
            IntegrationFileHandler fileHandler = getSupportFileHandler(this.uploadStoreType);
            storeType = this.uploadStoreType;
            storeInfo = fileHandler.upload(data, fileName);
        }
        long length = data.length;
        Date now = new Date();
        filecenterInfo.setStoreType(storeType);
        filecenterInfo.setStoreInfo(storeInfo);
        filecenterInfo.setUploadDate(now);
        filecenterInfo.setLength(length);
        filecenterInfo.setMd5(md5);
        filecenterInfo.setType(fileType);
        filecenterInfo.setFilename(fileName);
        this.filecenterInfoRepository.save(filecenterInfo);
        return FileInfo.builder()
                .id(filecenterInfo.getId())
                .length(length)
                .filename(fileName)
                .storeType(this.uploadStoreType)
                .storeInfo(storeInfo)
                .uploadDate(now)
                .type(fileType)
                .md5(md5)
                .build();
    }

    private IntegrationFileHandler getSupportFileHandler(String storeType) {
        for (IntegrationFileHandler fileHandler : fileHandlers) {
            if (fileHandler.support(storeType)) {
                return fileHandler;
            }
        }
        throw new RuntimeException("不支持的文件存储方式！");
    }


    @Override
    public FileInfo downloadZip(String ids) throws IOException {
        String zipName = "archive";
        String name = zipName + ".zip";
        String uuidDir = this.dir + UUID.randomUUID().toString().replaceAll("-", "");
        String tempDir = uuidDir + "/" + zipName;

        List<String> fileIdList = Arrays.asList(ids.split(","));
        Set<String> fileIdSet = new HashSet<>(fileIdList);
        File uuidDirFile = new File(uuidDir);
        uuidDirFile.mkdir();
        File file = new File(tempDir);
        file.mkdir();
        if (fileIdSet.size() == 0) {
            throw new RuntimeException("文件列表为空！");
        }
        for (String s1 : fileIdSet) {
            FileInfo fileInfo = this.download(s1);
            String path = tempDir + "/" + fileInfo.getFilename();
            File temp = new File(path);
            FileUtils.writeByteArrayToFile(temp, fileInfo.getData());
        }
        ZipUtil.zip(file, tempDir + "/" + name);
        File zipFile = new File(tempDir + "/" + name);
        byte[] bytes = FileUtils.readFileToByteArray(zipFile);
        FileUtils.deleteDirectory(file);
        return FileInfo.builder().data(bytes).filename(name).build();
    }


    @Override
    public void delete(String id) {
        this.filecenterInfoRepository.findById(id).ifPresent((one) -> {
            this.filecenterInfoRepository.delete(one);
        });
    }


}
