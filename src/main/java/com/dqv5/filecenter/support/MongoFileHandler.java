package com.dqv5.filecenter.support;

import com.dqv5.filecenter.enums.FileStoreType;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 文件保存到Mongodb
 *
 * @author duqian
 * @date 2019-08-20
 */
@Component
@Profile("mongo")
public class MongoFileHandler implements IntegrationFileHandler {

    @Resource
    private GridFSBucket gridFsBucket;
    @Resource
    private GridFsTemplate gridFsTemplate;

    /**
     * @param data     文件byte数组
     * @param fileName 文件名
     * @return
     */
    @Override
    public String upload(byte[] data, String fileName) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        String suffix = "unkown";
        int index = fileName.lastIndexOf(".");
        if (index > -1) {
            suffix = fileName.substring(index + 1);
        }
        ObjectId store = gridFsTemplate.store(inputStream, fileName, suffix);
        String objectId = store.toString();
        inputStream.close();
        return objectId;
    }

    /**
     * @param id 文件保存到MongoDB中生成的`_id`
     * @return
     * @throws IOException
     */
    @Override
    public byte[] download(String id) throws IOException {
        GridFSFile one = gridFsTemplate.findOne(query(where("_id").is(new ObjectId(id))));
        if (one == null) {
            return null;
        }
        GridFsResource gridFsResource = convertGridFsFile2Resource(one);
        InputStream inputStream = gridFsResource.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, outputStream);
        byte[] data = outputStream.toByteArray();
        outputStream.close();
        inputStream.close();
        return data;
    }


    @Override
    public boolean support(String storeType) {
        return FileStoreType.Mongo.getValue().equals(storeType);
    }

    private GridFsResource convertGridFsFile2Resource(GridFSFile gridFsFile) {
        GridFSDownloadStream gridFsDownloadStream = gridFsBucket.openDownloadStream(gridFsFile.getObjectId());
        return new GridFsResource(gridFsFile, gridFsDownloadStream);
    }
}
