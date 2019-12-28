package com.dqv5.filecenter.config;

import com.dqv5.filecenter.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author duq
 */
@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {
    @Resource
    private FileService fileService;

    /**
     * 每天晚上0点定时转移远程文件到当前存储方式
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void transferRemoteFile() throws IOException {
        fileService.transferRemoteFile();
    }
}
