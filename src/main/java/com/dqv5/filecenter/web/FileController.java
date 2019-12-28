package com.dqv5.filecenter.web;

import com.dqv5.filecenter.entity.FilecenterInfo;
import com.dqv5.filecenter.pojo.FileInfo;
import com.dqv5.filecenter.pojo.CommonReturn;
import com.dqv5.filecenter.pojo.CommonReturnEntity;
import com.dqv5.filecenter.service.FileService;
import com.dqv5.filecenter.util.MimeTypeUtils;
import com.github.wenhao.jpa.Specifications;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author duqian
 * @date 2019-08-20
 */
@RestController
@Slf4j
public class FileController {
    @Resource
    private FileService fileService;

    @ApiOperation(value = "获取文件中心总览信息")
    @GetMapping(value = "/status")
    public ResponseEntity<CommonReturnEntity> totalSize() {
        long size = fileService.totalSize();
        return CommonReturn.build(true, "操作成功", size);
    }

    @GetMapping("/files")
    public ResponseEntity<Page<FilecenterInfo>> queryListForPage(Integer pageNum, Integer pageSize, String filename, String type, String storeType, String md5) {
        Specification<FilecenterInfo> specification = Specifications.<FilecenterInfo>and()
                .like(StringUtils.isNotBlank(filename), "filename", "%" + filename + "%")
                .like(StringUtils.isNotBlank(type), "type", "%" + type + "%")
                .like(StringUtils.isNotBlank(md5), "md5", "%" + md5 + "%")
                .eq(StringUtils.isNotBlank(storeType), "storeType", storeType)
                .build();
        Sort sort = Sort.by(Sort.Direction.DESC, "uploadDate")
                .and(Sort.by(Sort.Direction.ASC, "id"));
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<FilecenterInfo> page = fileService.queryListForPage(specification, pageable);
        return ResponseEntity.ok(page);
    }


    @ApiOperation(value = "上传文件", notes = "上传文件,参数为上传的文件,若上传成功，会返回文件信息")
    @PostMapping("/file")
    public ResponseEntity<CommonReturnEntity> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        FileInfo fileInfo = this.fileService.upload(file);
        return CommonReturn.build(true, "文件上传成功！", fileInfo);
    }

    @ApiOperation(value = "获取文件", notes = "获取文件,参数为id")
    @ApiImplicitParam(name = "id", value = "文件标识", required = true, defaultValue = "5a435fd1dd5174442034c0fa", dataType = "String", paramType = "path")
    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> fileDownLoadV2(@PathVariable String id, String t) throws Exception {
        FileInfo fileInfo = fileService.download(id);
        byte[] bytes = fileInfo.getData();
        String fileName = fileInfo.getFilename();
        if (StringUtils.isNotBlank(t)) {
            return this.download(bytes, fileName, "attachment");
        }
        return this.download(bytes, fileName);
    }

    @ApiOperation(value = "删除文件", notes = "删除文件,参数为id,成功或失败信息在响应体中")
    @ApiImplicitParam(name = "id", value = "文件标识", required = true, defaultValue = "5a435fd1dd5174442034c0fa", dataType = "String", paramType = "path")
    @DeleteMapping("/file/{id}")
    public ResponseEntity<CommonReturnEntity> deleteGfsFile(@PathVariable String id) {
        this.fileService.delete(id);
        return CommonReturn.build(true, "删除成功");
    }

    @ApiOperation(value = "获取文件详情", notes = "参数为id")
    @ApiImplicitParam(name = "id", value = "文件标识", required = true, defaultValue = "5a435fd1dd5174442034c0fa", dataType = "String", paramType = "path")
    @GetMapping("/info/{id}")
    public ResponseEntity<CommonReturnEntity> fileDownLoadInfo(@PathVariable String id) {
        FileInfo fileInfo = this.fileService.getFileInfo(id);
        return CommonReturn.build(true, "获取文件详情成功！", fileInfo);
    }


    @ApiOperation(value = "打包下载文件", notes = "打包下载文件,参数为id拼接")
    @ApiImplicitParam(name = "ids", value = "文件标识", required = true, defaultValue = "aaa,bbb,ccc", dataType = "String", paramType = "path")
    @GetMapping("/zip")
    public ResponseEntity<byte[]> fileDownLoadByZip(String ids) throws IOException {
        FileInfo zipInfo = fileService.downloadZip(ids);
        return this.download(zipInfo.getData(), zipInfo.getFilename());
    }


    private ResponseEntity<byte[]> download(byte[] data, String fileName) throws UnsupportedEncodingException {
        return download(data, fileName, "inline");
    }

    private ResponseEntity<byte[]> download(byte[] data, String fileName, String dispositionType) throws UnsupportedEncodingException {
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition disposition = ContentDisposition.builder(dispositionType).
                filename(encodedFileName).build();
        headers.setContentDisposition(disposition);
        String suffix = "unknown";
        if (fileName.contains(".")) {
            suffix = fileName.substring(fileName.lastIndexOf("."));
        }
        String mimeType = MimeTypeUtils.getMimeTypeBySuffix(suffix);
        headers.setContentType(MediaType.valueOf(mimeType));
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

}
