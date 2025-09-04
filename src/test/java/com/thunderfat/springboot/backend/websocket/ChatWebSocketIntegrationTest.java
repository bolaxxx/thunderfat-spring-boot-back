package com.thunderfat.springboot.backend.websocket;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.thunderfat.springboot.backend.controllers.ChatWebSocketController.ChatMessage;
import com.thunderfat.springboot.backend.controllers.ChatWebSocketController.PresenceNotification;
import com.thunderfat.springboot.backend.controllers.ChatWebSocketController.TypingIndicator;

/**
 * Integration tests for WebSocket chat functionality.
 * Tests real-time messaging, presence notifications, and typing indicators.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("WebSocket Chat Integration Tests")
class ChatWebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private final String WEBSOCKET_URI = "ws://localhost:%d/ws/chat";
    private final String CHAT_ENDPOINT = "/app/chat/1/message";
    private final String CHAT_TOPIC = "/topic/chat/1";

    @BeforeEach
    void setUp() throws Exception {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        
        String url = String.format(WEBSOCKET_URI, port);
        stompSession = stompClient.connect(url, new TestStompSessionHandler()).get(10, TimeUnit.SECONDS);
    }

    // ================================
    // CHAT MESSAGE TESTS
    // ================================

    @Test
    @DisplayName("Should send and receive chat messages")
    void shouldSendAndReceiveChatMessages() throws Exception {
        // Given
        CountDownLatch messageReceived = new CountDownLatch(1);
        AtomicReference<ChatMessage> receivedMessage = new AtomicReference<>();

        // Subscribe to chat topic
        stompSession.subscribe(CHAT_TOPIC, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessage.set((ChatMessage) payload);
                messageReceived.countDown();
            }
        });

        // When
        ChatMessage message = new ChatMessage();
        message.setContent("Hello, this is a test message!");
        message.setMessageType("TEXT");
        
        stompSession.send(CHAT_ENDPOINT, message);

        // Then
        assertThat(messageReceived.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(receivedMessage.get()).isNotNull();
        assertThat(receivedMessage.get().getContent()).isEqualTo("Hello, this is a test message!");
        assertThat(receivedMessage.get().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should handle multiple concurrent messages")
    void shouldHandleMultipleConcurrentMessages() throws Exception {
        // Given
        int messageCount = 5;
        CountDownLatch messagesReceived = new CountDownLatch(messageCount);
        AtomicReference<Integer> receivedCount = new AtomicReference<>(0);

        // Subscribe to chat topic
        stompSession.subscribe(CHAT_TOPIC, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedCount.updateAndGet(count -> count + 1);
                messagesReceived.countDown();
            }
        });

        // When
        for (int i = 0; i < messageCount; i++) {
            ChatMessage message = new ChatMessage();
            message.setContent("Message " + i);
            stompSession.send(CHAT_ENDPOINT, message);
        }

        // Then
        assertThat(messagesReceived.await(10, TimeUnit.SECONDS)).isTrue();
        assertThat(receivedCount.get()).isEqualTo(messageCount);
    }

    // ================================
    // PRESENCE NOTIFICATION TESTS
    // ================================

    @Test
    @DisplayName("Should handle user join notifications")
    void shouldHandleUserJoinNotifications() throws Exception {
        // Given
        CountDownLatch notificationReceived = new CountDownLatch(1);
        AtomicReference<PresenceNotification> receivedNotification = new AtomicReference<>();

        // Subscribe to presence topic
        stompSession.subscribe("/topic/chat/1/presence", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return PresenceNotification.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedNotification.set((PresenceNotification) payload);
                notificationReceived.countDown();
            }
        });

        // When
        stompSession.send("/app/chat/1/join", "");

        // Then
        assertThat(notificationReceived.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(receivedNotification.get()).isNotNull();
        assertThat(receivedNotification.get().getStatus()).isEqualTo("JOINED");
        assertThat(receivedNotification.get().getChatId()).isEqualTo("1");
    }

    @Test
    @DisplayName("Should handle user leave notifications")
    void shouldHandleUserLeaveNotifications() throws Exception {
        // Given
        CountDownLatch notificationReceived = new CountDownLatch(1);
        AtomicReference<PresenceNotification> receivedNotification = new AtomicReference<>();

        // Subscribe to presence topic
        stompSession.subscribe("/topic/chat/1/presence", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return PresenceNotification.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedNotification.set((PresenceNotification) payload);
                notificationReceived.countDown();
            }
        });

        // When
        stompSession.send("/app/chat/1/leave", "");

        // Then
        assertThat(notificationReceived.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(receivedNotification.get()).isNotNull();
        assertThat(receivedNotification.get().getStatus()).isEqualTo("LEFT");
    }

    // ================================
    // TYPING INDICATOR TESTS
    // ================================

    @Test
    @DisplayName("Should handle typing indicators")
    void shouldHandleTypingIndicators() throws Exception {
        // Given
        CountDownLatch typingReceived = new CountDownLatch(1);
        AtomicReference<TypingIndicator> receivedIndicator = new AtomicReference<>();

        // Subscribe to typing topic
        stompSession.subscribe("/topic/chat/1/typing", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return TypingIndicator.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedIndicator.set((TypingIndicator) payload);
                typingReceived.countDown();
            }
        });

        // When
        TypingIndicator indicator = new TypingIndicator();
        indicator.setTyping(true);
        
        stompSession.send("/app/chat/1/typing", indicator);

        // Then
        assertThat(typingReceived.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(receivedIndicator.get()).isNotNull();
        assertThat(receivedIndicator.get().isTyping()).isTrue();
        assertThat(receivedIndicator.get().getChatId()).isEqualTo("1");
    }

    // ================================
    // CONNECTION TESTS
    // ================================

    @Test
    @DisplayName("Should establish WebSocket connection successfully")
    void shouldEstablishWebSocketConnectionSuccessfully() {
        assertThat(stompSession).isNotNull();
        assertThat(stompSession.isConnected()).isTrue();
    }

    @Test
    @DisplayName("Should handle connection disconnection gracefully")
    void shouldHandleConnectionDisconnectionGracefully() throws Exception {
        // Given
        assertThat(stompSession.isConnected()).isTrue();

        // When
        stompSession.disconnect();

        // Then
        await().atMost(Duration.ofSeconds(5))
               .until(() -> !stompSession.isConnected());
    }

    @Test
    @DisplayName("Should support multiple concurrent connections")
    void shouldSupportMultipleConcurrentConnections() throws Exception {
        // Given
        String url = String.format(WEBSOCKET_URI, port);
        
        // When - Create additional connections
        StompSession session2 = stompClient.connect(url, new TestStompSessionHandler()).get(5, TimeUnit.SECONDS);
        StompSession session3 = stompClient.connect(url, new TestStompSessionHandler()).get(5, TimeUnit.SECONDS);

        // Then
        assertThat(stompSession.isConnected()).isTrue();
        assertThat(session2.isConnected()).isTrue();
        assertThat(session3.isConnected()).isTrue();

        // Cleanup
        session2.disconnect();
        session3.disconnect();
    }

    // ================================
    // ERROR HANDLING TESTS
    // ================================

    @Test
    @DisplayName("Should handle invalid message destinations")
    void shouldHandleInvalidMessageDestinations() throws Exception {
        // Given
        ChatMessage message = new ChatMessage();
        message.setContent("Test message");

        // When & Then - Should not throw exception
        assertThatCode(() -> {
            stompSession.send("/app/invalid/destination", message);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle malformed messages gracefully")
    void shouldHandleMalformedMessagesGracefully() throws Exception {
        // When & Then - Should not throw exception
        assertThatCode(() -> {
            stompSession.send(CHAT_ENDPOINT, "invalid-message-format");
        }).doesNotThrowAnyException();
    }

    // ================================
    // PERFORMANCE TESTS
    // ================================

    @Test
    @DisplayName("Should handle high-frequency messages")
    void shouldHandleHighFrequencyMessages() throws Exception {
        // Given
        int messageCount = 50;
        CountDownLatch messagesReceived = new CountDownLatch(messageCount);
        long startTime = System.currentTimeMillis();

        // Subscribe to chat topic
        stompSession.subscribe(CHAT_TOPIC, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messagesReceived.countDown();
            }
        });

        // When
        for (int i = 0; i < messageCount; i++) {
            ChatMessage message = new ChatMessage();
            message.setContent("Performance test message " + i);
            stompSession.send(CHAT_ENDPOINT, message);
        }

        // Then
        assertThat(messagesReceived.await(30, TimeUnit.SECONDS)).isTrue();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should process all messages within reasonable time (30 seconds)
        assertThat(duration).isLessThan(30000);
    }

    // ================================
    // HELPER CLASSES
    // ================================

    private static class TestStompSessionHandler extends StompSessionHandlerAdapter {
        @Override
        public void handleException(StompSession session, StompCommand command, 
                                  StompHeaders headers, byte[] payload, Throwable exception) {
            exception.printStackTrace();
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("Connected to WebSocket");
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            exception.printStackTrace();
        }
    }
}
