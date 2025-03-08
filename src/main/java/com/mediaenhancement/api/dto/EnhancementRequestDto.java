package com.mediaenhancement.api.dto;

import com.mediaenhancement.api.model.EnhancementRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnhancementRequestDto {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "File is required")
    private MultipartFile file;
    
    @NotNull(message = "Media type is required")
    private EnhancementRequest.MediaType mediaType;
    
    private EnhancementRequest.Priority priority = EnhancementRequest.Priority.MEDIUM;
} 