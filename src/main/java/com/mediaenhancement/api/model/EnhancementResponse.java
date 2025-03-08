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
public class EnhancementResponse {
    
    private UUID requestId;
    private String userId;
    private String fileName;
    private byte[] enhancedData;
    private EnhancementRequest.MediaType mediaType;
    private EnhancementRequest.ProcessingStatus status;
    private LocalDateTime completionTime;
    private String message;
} 