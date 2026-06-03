package org.example.agentserver.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.agentserver.model.dto.ProductReviewRequest;
import org.example.agentserver.service.AiChatService;
import org.example.agentserver.service.ProductReviewService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final AiChatService aiChatService;
    private final ProductReviewService productReviewService;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        String query = uri.getQuery();
        String userId = extractParam(query, "userId");
        String productIdStr = extractParam(query, "productId");

        if (userId == null || productIdStr == null) {
            sendError(session, "缺少必要参数: userId, productId");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        Long productId;
        try {
            productId = Long.parseLong(productIdStr);
        } catch (NumberFormatException e) {
            sendError(session, "productId格式错误");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        String sessionId = aiChatService.createSession(userId, productId);
        session.getAttributes().put("sessionId", sessionId);
        session.getAttributes().put("userId", userId);
        session.getAttributes().put("productId", productId);
        sessionMap.put(sessionId, session);

        ProductReviewRequest productInfo = productReviewService.buildReviewRequest(productId);
        String contextMsg = "以下是需要审核的商品信息：\n" +
                "名称：" + productInfo.getName() + "\n" +
                "描述：" + productInfo.getDescription() + "\n" +
                "商家：" + productInfo.getMerchantName() + "\n" +
                "分类：" + productInfo.getCategoryName();
        aiChatService.chat(sessionId, contextMsg);

        Map<String, Object> welcome = new java.util.HashMap<>();
        welcome.put("type", "connected");
        welcome.put("sessionId", sessionId);
        welcome.put("message", "AI商品审核助手已连接，请提出您的问题");
        sendMessage(session, welcome);

        log.info("WebSocket连接建立: userId={}, productId={}, sessionId={}", userId, productId, sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = (String) session.getAttributes().get("sessionId");
        if (sessionId == null) {
            sendError(session, "会话不存在");
            return;
        }

        String payload = message.getPayload();
        Map<String, Object> result = aiChatService.chat(sessionId, payload);

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("type", "message");
        response.put("reply", result.get("reply"));
        sendMessage(session, response);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = (String) session.getAttributes().get("sessionId");
        if (sessionId != null) {
            sessionMap.remove(sessionId);
            aiChatService.removeSession(sessionId);
        }
        log.info("WebSocket连接关闭: sessionId={}, status={}", sessionId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: sessionId={}", session.getAttributes().get("sessionId"), exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    private String extractParam(String query, String param) {
        if (query == null) return null;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(param)) {
                return kv[1];
            }
        }
        return null;
    }

    private void sendMessage(WebSocketSession session, Object data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("发送WebSocket消息失败", e);
        }
    }

    private void sendError(WebSocketSession session, String error) {
        Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("type", "error");
        msg.put("message", error);
        sendMessage(session, msg);
    }
}
