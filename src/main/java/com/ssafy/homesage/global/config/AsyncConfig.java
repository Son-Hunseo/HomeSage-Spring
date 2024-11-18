package com.ssafy.homesage.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private int CORE_POOL_SIZE = 100;

    @Bean(name = "sampleExecutor")
    public Executor threadPoolTaskExecutor(){

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        taskExecutor.setMaxPoolSize(CORE_POOL_SIZE);
        taskExecutor.setQueueCapacity(CORE_POOL_SIZE);
        taskExecutor.setThreadNamePrefix("Executor-");

        taskExecutor.initialize();

        return taskExecutor;
    }

}

