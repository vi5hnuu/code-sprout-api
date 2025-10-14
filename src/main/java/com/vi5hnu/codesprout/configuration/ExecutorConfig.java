package com.vi5hnu.codesprout.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorConfig {
    
    /**
     * Formula for CPU-bound tasks:
     * Pool Size = Number of CPU Cores + 1
     */
    @Bean(name = "cpuBoundExecutor")
    public Executor cpuBoundExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores + 1);
        executor.setMaxPoolSize(cores + 1);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("CPU-");
        executor.initialize();
        return executor;
    }
    
    /**
     * Formula for I/O-bound tasks:
     * Pool Size = Number of CPU Cores × (1 + Wait Time / Compute Time)
     * 
     * Example: 8 cores, 90% wait time (network I/O)
     * Pool Size = 8 × (1 + 0.9/0.1) = 8 × 10 = 80
     */
    @Bean(name = "ioBoundExecutor")
    public Executor ioBoundExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        double waitToComputeRatio = 9.0; // 90% wait, 10% compute
        int poolSize = (int) (cores * (1 + waitToComputeRatio));
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize * 2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("IO-");
        executor.setRejectedExecutionHandler(
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.initialize();
        return executor;
    }
}