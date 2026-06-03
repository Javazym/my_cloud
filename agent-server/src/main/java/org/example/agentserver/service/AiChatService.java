package org.example.agentserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AiChatService {

    private final ApiProxyService proxyService;

    private final ConcurrentHashMap<String, ChatSession> sessions = new ConcurrentHashMap<>();

    public String createSession(String userId, Long productId) {
        String sessionId = userId + ":" + UUID.randomUUID();
        ChatSession session = new ChatSession(userId, productId, sessionId);
        sessions.put(sessionId, session);
        return sessionId;
    }

    public Map<String, Object> chat(String sessionId, String message) {
        ChatSession session = sessions.get(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在或已过期: " + sessionId);
        }
        session.updateLastAccessTime();

        Map<String, Object> request = new java.util.HashMap<>();
        request.put("sessionId", sessionId);
        request.put("message", message);

        return proxyService.post("/api/chat", request);
    }

    public ChatSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public static class ChatSession {
        private final String userId;
        private final Long productId;
        private final String sessionId;
        private long lastAccessTime;

        public ChatSession(String userId, Long productId, String sessionId) {
            this.userId = userId;
            this.productId = productId;
            this.sessionId = sessionId;
            this.lastAccessTime = System.currentTimeMillis();
        }

        public void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        public String getUserId() { return userId; }
        public Long getProductId() { return productId; }
        public String getSessionId() { return sessionId; }
        public long getLastAccessTime() { return lastAccessTime; }
    }
}
