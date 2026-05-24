package com.homepulse.hma.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatService(ChatClient.Builder chatClientBuilder, MaintenanceTools maintenanceTools) {
        log.info("Initializing ChatService and configuring ChatClient with memory and tools.");
        this.chatMemory = new InMemoryChatMemory();
        
        this.chatClient = chatClientBuilder
                .defaultSystem("You are a helpful home maintenance assistant (HMA).\n" +
                        "Your job is to help users log their home maintenance tasks and retrieve details of past maintenance.\n" +
                        "Always ask for clarification if you are unsure about the specific item/appliance or the action performed.\n" +
                        "Make sure to use the tools provided to save the maintenance tasks to the database, and confirm the details once saved.\n" +
                        "If you retrieve logs, format them clearly for the user.")
                .defaultTools(maintenanceTools)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    /**
     * Stateless call or default conversation session.
     */
    public String chat(String userMessage) {
        return chat(userMessage, "default-session");
    }

    /**
     * Stateful chat session with a specific conversation ID.
     */
    public String chat(String userMessage, String conversationId) {
        log.info("Sending chat prompt to AI agent. Session: '{}', Message length: {}", conversationId, userMessage.length());
        String response = chatClient.prompt()
                .user(userMessage)
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
        log.info("AI response received for session '{}'. Response length: {}", conversationId, response != null ? response.length() : 0);
        return response;
    }
}
