package com.mediaenhancement.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancementRequest implements Comparable<EnhancementRequest> {
    
    private UUID id;
    private String userId;
    private String fileName;
    private byte[] fileData;
    private MediaType mediaType;
    private Priority priority;
    private LocalDateTime submissionTime;
    private ProcessingStatus status;
    
    @Override
    public int compareTo(EnhancementRequest other) {
        // First compare by priority (higher priority comes first)
        int priorityComparison = other.priority.getValue() - this.priority.getValue();
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        
        // If same priority, compare by submission time (earlier comes first)
        return this.submissionTime.compareTo(other.submissionTime);
    }
    
    public enum MediaType {
        IMAGE,
        VIDEO
    }
    
    public enum Priority {
        LOW(1),
        MEDIUM(2),
        HIGH(3);
        
        private final int value;
        
        Priority(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public enum ProcessingStatus {
        QUEUED,
        PROCESSING,
        COMPLETED,
        FAILED
    }
} 