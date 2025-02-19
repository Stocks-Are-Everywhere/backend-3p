package org.scoula.three_people.order.websocket.handler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.scoula.three_people.order.websocket.dto.OrderEventMessage;
import org.scoula.three_people.order.websocket.dto.TradeExecutionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TradeWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Autowired
    public TradeWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("New WebSocket connection established: {}", session.getId());
        try  {
            session.sendMessage(
                    new TextMessage("웹소켓 연결 성공"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("WebSocket connection closed: {}", session.getId());
    }

    public void broadcastTradeExecution(TradeExecutionMessage message) {
        String payload = convertToJson(message);
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            } catch (IOException e) {
                log.error("Error sending message to WebSocket session {}", session.getId(), e);
            }
        });
    }

    public void broadcastOrderEvent(OrderEventMessage message) {
        String payload = convertToJson(message);
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            } catch (IOException e) {
                log.error("Error sending OrderEventMessage to WebSocket session {}", session.getId(), e);
            }
        });
    }

    private String convertToJson(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Error converting message to JSON", e);
            return String.format("{\"error\": \"%s\"}", e.getMessage());
        }
    }
}
