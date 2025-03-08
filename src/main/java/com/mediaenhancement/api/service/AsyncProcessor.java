package com.mediaenhancement.api.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Helper class to solve the @Async self-invocation limitation.
 * This class provides a way to call @Async methods from another bean,
 * avoiding the self-invocation problem.
 */
@Component
public class AsyncProcessor {
    
    /**
     * Starts the processing thread asynchronously.
     * This method is called from EnhancementServiceImpl's @PostConstruct method.
     * 
     * @param service The service that contains the processing logic
     */
    @Async
    public void startProcessingThread(EnhancementServiceImpl service) {
        service.startProcessingThread();
    }
} 