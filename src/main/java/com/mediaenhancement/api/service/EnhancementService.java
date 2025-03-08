package com.mediaenhancement.api.service;

import com.mediaenhancement.api.dto.EnhancementRequestDto;
import com.mediaenhancement.api.model.EnhancementRequest;
import com.mediaenhancement.api.model.EnhancementResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface EnhancementService {
    
    UUID submitEnhancementRequest(EnhancementRequestDto requestDto) throws IOException;
    
    EnhancementResponse getEnhancementResult(UUID requestId);
    
    List<EnhancementRequest> getAllRequests(String userId);
    
    void cancelRequest(UUID requestId);
} 