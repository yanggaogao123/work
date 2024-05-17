package com.gci.schedule.driverless.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.gci.schedule.driverless.timingwheel.TimingWheel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.*;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2023-06-13 16:03
 * @version: v1.0
 * @Modified by:
 **/
@Configuration
public class ThreadConfig {

    @Value("${dispatch.schedule.poolsize}")
    private String SCHEDULE_POOLSIZE;

    @Value("${dispatch.async.poolsize}")
    private String ASYNC_POOLSIZE;

    @Value("${dispatch.kafka.poolsize}")
    private String KAFKA_POOLSIZE;

    @Value("${dispatch.timingwheel.corepoolsize}")
    private String TIMINGWHEEL_COREPOOLSIZE;
    @Value("${dispatch.timingwheel.maximumpoolsize}")
    private String TIMINGWHEEL_MAXIMUMPOOLSIZE;
    @Value("${dispatch.timingwheel.keepalivetime}")
    private String TIMINGWHEEL_KEEPALIVETIME;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Integer.parseInt(SCHEDULE_POOLSIZE));
        return scheduler;
    }

    @Bean
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        int poolSize = Integer.parseInt(ASYNC_POOLSIZE);
        taskExecutor.setCorePoolSize(poolSize);
        taskExecutor.setMaxPoolSize(poolSize * 4);
        taskExecutor.setKeepAliveSeconds(240);
        taskExecutor.setQueueCapacity(10000);
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.setThreadNamePrefix("asyncExecutor-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public ExecutorService dispatchExecutorService() {
        return new ThreadPoolExecutor(
                Integer.parseInt(TIMINGWHEEL_COREPOOLSIZE),
                Integer.parseInt(TIMINGWHEEL_MAXIMUMPOOLSIZE),
                Integer.parseInt(TIMINGWHEEL_KEEPALIVETIME), TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(), ThreadFactoryBuilder.create().setNamePrefix("timingWheel-").build(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Bean
    public TimingWheel timingWheel() {
        return new TimingWheel(dispatchExecutorService(), 512);
    }

    @Bean
    public ExecutorService kafkaExecutorService() {
        return Executors.newFixedThreadPool(Integer.parseInt(KAFKA_POOLSIZE));
    }

}
