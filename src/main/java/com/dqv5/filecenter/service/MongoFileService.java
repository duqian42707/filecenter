package com.dqv5.filecenter.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author duq
 */
@Service
@Profile("mongo")
public class MongoFileService {
    @Resource
    private GridFSBucket gridFsBucket;
    @Autowired
    GridFsTemplate gridFsTemplate;

    //上传文件
    public String storeFileInGridFs(String fileName, InputStream inputStream) throws IOException {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        ObjectId store = gridFsTemplate.store(inputStream, fileName, suffix);
        return store.toString();
    }

    // 下载文件
    public GridFsResource findFilesInGridFs(String id) throws IOException {

        GridFSFile one = gridFsTemplate.findOne(query(where("_id").is(new ObjectId(id))));
        if (one != null) {
            GridFsResource gridFsResource = convertGridFsFile2Resource(one);
            return gridFsResource;
        } else {
            return null;
        }
    }

    // 查文件信息
    public GridFSFile findFilesInfo(String id) throws IOException {

        GridFSFile one = gridFsTemplate.findOne(query(where("_id").is(new ObjectId(id))));
        if (one != null) {
            return one;
        } else {
            return null;
        }
    }

    // 删除文件
    public Boolean deleteFilesInGridFs(String id) throws IOException {
        gridFsTemplate.delete(query(where("_id").is(new ObjectId(id))));
        GridFSFile one = gridFsTemplate.findOne(query(where("_id").is(new ObjectId(id))));
        if (one == null) {
            return true;
        } else {
            return false;
        }
    }

    // 所有文件
    public void readFilesFromGridFs() {
        GridFsResource[] txtFiles = gridFsTemplate.getResources("*");
        for (GridFsResource txtFile : txtFiles) {
            System.out.println(txtFile.getFilename());
        }
    }

    public GridFsResource convertGridFsFile2Resource(GridFSFile gridFsFile) {
        GridFSDownloadStream gridFsDownloadStream = gridFsBucket.openDownloadStream(gridFsFile.getObjectId());
        return new GridFsResource(gridFsFile, gridFsDownloadStream);
    }

}
