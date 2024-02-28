package dataExtraction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年11月27日
 * 定时任务线程池配置
 */
@Configuration
public class ScheduleConfig {
    /**
     * 定时任务线程池
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        threadPoolTaskScheduler.setThreadNamePrefix("schedulerTask-");
        return threadPoolTaskScheduler;
    }

}
