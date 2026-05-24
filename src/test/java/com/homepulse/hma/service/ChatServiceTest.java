package com.homepulse.hma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock(answer = Answers.RETURNS_SELF)
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private MaintenanceTools maintenanceTools;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);

        chatService = new ChatService(chatClientBuilder, maintenanceTools);
    }

    @Test
    void testChatStateless() {
        when(chatClient.prompt()
                .user("hello")
                .advisors(any(Consumer.class))
                .call()
                .content())
                .thenReturn("hi standard");

        String reply = chatService.chat("hello");
        assertEquals("hi standard", reply);
    }

    @Test
    void testChatStateful() {
        when(chatClient.prompt()
                .user("hello state")
                .advisors(any(Consumer.class))
                .call()
                .content())
                .thenReturn("hi state reply");

        String reply = chatService.chat("hello state", "custom-session");
        assertEquals("hi state reply", reply);
    }

    @Test
    void testChatWithNullResponse() {
        when(chatClient.prompt()
                .user("hello null")
                .advisors(any(Consumer.class))
                .call()
                .content())
                .thenReturn(null);

        String reply = chatService.chat("hello null");
        assertEquals(null, reply);
    }
}
