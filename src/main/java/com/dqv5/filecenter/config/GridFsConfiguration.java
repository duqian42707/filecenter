package com.dqv5.filecenter.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.internal.MongoClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author duq
 */
@Configuration
@EnableMongoRepositories
@Profile("mongo")
public class GridFsConfiguration extends AbstractMongoClientConfiguration {
    @Value("${filecenter.mongo.database}")
    private String database;
    @Value("${filecenter.mongo.host}")
    private String host;
    @Value("${filecenter.mongo.port}")
    private Integer port;

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    public GridFSBucket getGridFsBuckets() {
        MongoDatabase db = mongoDbFactory().getDb();
        return GridFSBuckets.create(db);
    }

    @Override
    protected String getDatabaseName() {
        return this.database;
    }


    @Override
    public MongoClient mongoClient() {
        String url = "mongodb://" + host + ":" + port;
        return MongoClients.create();
    }

    @Override
    @Bean
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoClientDbFactory(mongoClient(), getDatabaseName());
    }

}
