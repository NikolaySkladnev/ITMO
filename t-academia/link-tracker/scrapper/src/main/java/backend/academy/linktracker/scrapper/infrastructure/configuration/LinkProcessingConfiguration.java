package backend.academy.linktracker.scrapper.infrastructure.configuration;

import backend.academy.linktracker.scrapper.infrastructure.properties.SchedulerProperties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinkProcessingConfiguration {

    @Bean(name = "linkUpdatesExecutor", destroyMethod = "shutdown")
    public ExecutorService linkUpdatesExecutor(SchedulerProperties schedulerProperties) {
        AtomicInteger counter = new AtomicInteger(1);

        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("link-updates-" + counter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        };

        return Executors.newFixedThreadPool(schedulerProperties.getParallelism(), threadFactory);
    }
}
