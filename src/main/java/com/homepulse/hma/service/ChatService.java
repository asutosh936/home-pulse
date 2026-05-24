package com.homepulse.hma.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatService(ChatClient.Builder chatClientBuilder, MaintenanceTools maintenanceTools) {
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
        return chatClient.prompt()
                .user(userMessage)
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
    }
}
