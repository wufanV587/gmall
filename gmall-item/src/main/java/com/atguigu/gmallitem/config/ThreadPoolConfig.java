package com.atguigu.gmallitem.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){

        return new ThreadPoolExecutor(1000,5000,2,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(Integer.MAX_VALUE/10));
    }

}
