package com.milk.service.impl;

import com.milk.entity.ChatMessage;
import com.milk.entity.ChatSession;
import com.milk.mapper.ChatMessageMapper;
import com.milk.mapper.ChatSessionMapper;
import com.milk.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: ChatServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/13 10:59
 * @Version 1.0
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageMapper messageMapper;

    @Autowired
    private ChatSessionMapper sessionMapper;

    public void saveMessage(ChatMessage msg) {
        msg.setSessionId(msg.getReceiverId()+msg.getSenderId());
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }

    public void updateSession(ChatMessage msg) {
        // 查找是否存在会话
        ChatSession session = sessionMapper.findByUserAndSeller(msg.getSenderId(), msg.getReceiverId());
        if (session == null) {
            session = new ChatSession();
            session.setUserId(msg.getSenderId());
            session.setSellerId(msg.getReceiverId());
            session.setLastMessage(msg.getContent());
            session.setLastTime(LocalDateTime.now());
            session.setUnreadCount(1);
            session.setCreateTime(LocalDateTime.now());
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.insert(session);
        } else {
            session.setLastMessage(msg.getContent());
            session.setLastTime(LocalDateTime.now());
            session.setUnreadCount(session.getUnreadCount() + 1);
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.update(session);
        }
    }

    @Override
    public List<ChatMessage> getHistory(Long userId, Long sellerId) {
        List<ChatMessage> chatMessageList=messageMapper.getAllMsg(userId,sellerId);
        return chatMessageList;
    }
}
