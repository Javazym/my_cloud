package org.example.authserver.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageWrapper<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Builder.Default
    private String messageId = UUID.randomUUID().toString();
    private T data;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Builder.Default
    private Map<String, Object> headers = new HashMap<>();
    
    @Builder.Default
    private Integer retryCount = 0;
    
    @Builder.Default
    private Integer maxRetryCount = 3;
    
    private String sourceService;
    
    private String targetService;
    
    public void addHeader(String key, Object value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
    }
    
    public Object getHeader(String key) {
        return this.headers != null ? this.headers.get(key) : null;
    }
    
    public boolean canRetry() {
        return this.retryCount < this.maxRetryCount;
    }
    
    public void incrementRetry() {
        this.retryCount++;
    }
}
