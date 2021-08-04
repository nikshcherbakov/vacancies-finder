package com.nikshcherbakov.vacanciesfinder.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncExecutorConfig {

    @Value("${app.searchvacancies.defaults.threads.corepoolsize}")
    private Integer corePoolSize;

    @Value("${app.searchvacancies.defaults.threads.maxpoolsize}")
    private Integer maxPoolSize;

    @Value("${app.searchvacancies.defaults.threads.queuecapacity}")
    private Integer queueCapacity;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("JobSearchThread-");
        executor.initialize();
        return executor;
    }

}
