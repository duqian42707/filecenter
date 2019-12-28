package com.dqv5.filecenter.support;

import com.dqv5.filecenter.enums.FileStoreType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 文件保存在远程，只支持通过url下载，不支持上传
 *
 * @author duqian
 * @date 2019-08-21
 */
@Component
public class RemoteFileHandler extends AbstractFileHandler {

    private FileStoreType fileStoreType = FileStoreType.remote;

    @Resource
    private RestTemplate restTemplate;


    @Override
    public String upload(byte[] data, String fileName) {
        throw new RuntimeException("该存储方式不支持上传！");
    }

    @Override
    public FileStoreType getFileStoreType() {
        return this.fileStoreType;
    }

    /**
     * 从远程下载文件，只需知道文件的下载地址即可。
     * 将来可以考虑下载过一次之后，再把文件以当前的存储方式保存起来。
     *
     * @param url 文件的下载地址
     * @return
     */
    @Override
    public byte[] download(String url) {
        byte[] bytes = this.restTemplate.getForObject(url, byte[].class);
        if (bytes == null) {
            bytes = new byte[0];
        }
        return bytes;
    }

}
