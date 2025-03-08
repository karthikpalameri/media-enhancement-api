package com.mediaenhancement.api.service;

import com.mediaenhancement.api.dto.EnhancementRequestDto;
import com.mediaenhancement.api.model.EnhancementRequest;
import com.mediaenhancement.api.model.EnhancementResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnhancementServiceImpl implements EnhancementService {

    private final PriorityBlockingQueue<EnhancementRequest> requestQueue = new PriorityBlockingQueue<>();
    private final Map<UUID, EnhancementResponse> completedRequests = new ConcurrentHashMap<>();
    private final Map<UUID, EnhancementRequest> allRequests = new ConcurrentHashMap<>();
    
    @Value("${media.enhancement.processing.threads:1}")
    private int processingThreads;
    
    // Inject the AsyncProcessor to avoid self-invocation issue
    private final AsyncProcessor asyncProcessor;

    @PostConstruct
    public void init() {
        // Start the processing thread via the AsyncProcessor
        asyncProcessor.startProcessingThread(this);
    }

    @Override
    public UUID submitEnhancementRequest(EnhancementRequestDto requestDto) throws IOException {
        EnhancementRequest request = EnhancementRequest.builder()
                .id(UUID.randomUUID())
                .userId(requestDto.getUserId())
                .fileName(requestDto.getFile().getOriginalFilename())
                .fileData(requestDto.getFile().getBytes())
                .mediaType(requestDto.getMediaType())
                .priority(requestDto.getPriority())
                .submissionTime(LocalDateTime.now())
                .status(EnhancementRequest.ProcessingStatus.QUEUED)
                .build();

        requestQueue.add(request);
        allRequests.put(request.getId(), request);
        
        log.info("Submitted enhancement request: {} with priority: {}", request.getId(), request.getPriority());
        
        return request.getId();
    }

    @Override
    public EnhancementResponse getEnhancementResult(UUID requestId) {
        // Check if the request is completed
        if (completedRequests.containsKey(requestId)) {
            return completedRequests.get(requestId);
        }
        
        // If not completed, check if it's in the queue or processing
        if (allRequests.containsKey(requestId)) {
            EnhancementRequest request = allRequests.get(requestId);
            return EnhancementResponse.builder()
                    .requestId(requestId)
                    .userId(request.getUserId())
                    .fileName(request.getFileName())
                    .mediaType(request.getMediaType())
                    .status(request.getStatus())
                    .message("Request is " + request.getStatus().toString().toLowerCase())
                    .build();
        }
        
        // If not found
        return EnhancementResponse.builder()
                .requestId(requestId)
                .status(EnhancementRequest.ProcessingStatus.FAILED)
                .message("Request not found")
                .build();
    }

    @Override
    public List<EnhancementRequest> getAllRequests(String userId) {
        return allRequests.values().stream()
                .filter(request -> request.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void cancelRequest(UUID requestId) {
        if (allRequests.containsKey(requestId)) {
            EnhancementRequest request = allRequests.get(requestId);
            
            // Only cancel if it's still in the queue
            if (request.getStatus() == EnhancementRequest.ProcessingStatus.QUEUED) {
                requestQueue.remove(request);
                request.setStatus(EnhancementRequest.ProcessingStatus.FAILED);
                
                // Add to completed with failed status
                completedRequests.put(requestId, EnhancementResponse.builder()
                        .requestId(requestId)
                        .userId(request.getUserId())
                        .fileName(request.getFileName())
                        .mediaType(request.getMediaType())
                        .status(EnhancementRequest.ProcessingStatus.FAILED)
                        .message("Request cancelled by user")
                        .completionTime(LocalDateTime.now())
                        .build());
                
                log.info("Cancelled enhancement request: {}", requestId);
            }
        }
    }

    // Make this method public so it can be called from AsyncProcessor
    public void startProcessingThread() {
        log.info("Starting media enhancement processing thread");
        
        while (true) {
            try {
                // Take the next request from the queue (blocks if queue is empty)
                EnhancementRequest request = requestQueue.take();
                
                // Update status to processing
                request.setStatus(EnhancementRequest.ProcessingStatus.PROCESSING);
                log.info("Processing request: {} for user: {}", request.getId(), request.getUserId());
                
                // Simulate processing delay (5 seconds)
                processMedia(request);
                
                // Update status to completed
                request.setStatus(EnhancementRequest.ProcessingStatus.COMPLETED);
                
                // Store the enhanced result
                EnhancementResponse response = EnhancementResponse.builder()
                        .requestId(request.getId())
                        .userId(request.getUserId())
                        .fileName(request.getFileName())
                        .enhancedData(request.getFileData()) // In a real implementation, this would be the enhanced data
                        .mediaType(request.getMediaType())
                        .status(EnhancementRequest.ProcessingStatus.COMPLETED)
                        .completionTime(LocalDateTime.now())
                        .message("Enhancement completed successfully")
                        .build();
                
                completedRequests.put(request.getId(), response);
                log.info("Completed processing request: {}", request.getId());
                
            } catch (InterruptedException e) {
                log.error("Processing thread interrupted", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error processing media enhancement request", e);
            }
        }
    }
    
    private void processMedia(EnhancementRequest request) throws InterruptedException {
        // Simulate processing delay of 5 seconds
        Thread.sleep(5000);
        
        // In a real implementation, this is where you would apply the enhancement algorithms
        log.info("Simulated processing for request: {} completed", request.getId());
    }
} 