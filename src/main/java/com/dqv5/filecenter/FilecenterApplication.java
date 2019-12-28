package com.dqv5.filecenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author duq
 */
@EnableJpaAuditing
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class FilecenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilecenterApplication.class, args);
    }

}
