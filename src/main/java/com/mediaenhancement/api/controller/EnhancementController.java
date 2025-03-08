package com.mediaenhancement.api.controller;

import com.mediaenhancement.api.dto.EnhancementRequestDto;
import com.mediaenhancement.api.model.EnhancementRequest;
import com.mediaenhancement.api.model.EnhancementResponse;
import com.mediaenhancement.api.service.EnhancementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enhancement")
@RequiredArgsConstructor
@Slf4j
public class EnhancementController {

    private final EnhancementService enhancementService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> submitEnhancementRequest(
            @ModelAttribute @Valid EnhancementRequestDto requestDto) {
        try {
            UUID requestId = enhancementService.submitEnhancementRequest(requestDto);
            return ResponseEntity.accepted().body(requestId);
        } catch (IOException e) {
            log.error("Error processing file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<EnhancementResponse> getEnhancementResult(@PathVariable UUID requestId) {
        EnhancementResponse response = enhancementService.getEnhancementResult(requestId);
        
        if (response.getStatus() == EnhancementRequest.ProcessingStatus.FAILED && 
                "Request not found".equals(response.getMessage())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnhancementRequest>> getUserRequests(@PathVariable String userId) {
        List<EnhancementRequest> requests = enhancementService.getAllRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> cancelRequest(@PathVariable UUID requestId) {
        enhancementService.cancelRequest(requestId);
        return ResponseEntity.noContent().build();
    }
} 