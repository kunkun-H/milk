package com.milk.service;

import com.milk.entity.ChatMessage;

import java.util.List;

/**
 * ClassName: ChatService
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/13 10:57
 * @Version 1.0
 */
public interface ChatService {
    void saveMessage(ChatMessage msg);

    void updateSession(ChatMessage msg);

    List<ChatMessage> getHistory(Long userId, Long sellerId);
}
