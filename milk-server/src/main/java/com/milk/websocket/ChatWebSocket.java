package com.milk.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.milk.entity.ChatMessage;
import com.milk.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/chat/{userId}")  // 每个用户的连接
public class ChatWebSocket {

    private static ChatService chatService; // 解决静态注入问题
    @Autowired
    public void setChatService(ChatService service) {
        chatService = service;
    }

    // 当前在线连接
    private static final ConcurrentHashMap<Long, Session> SESSION_POOL = new ConcurrentHashMap<>();

    private Long userId;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        this.userId = userId;
        SESSION_POOL.put(userId, session);
        System.out.println("用户 " + userId + " 已连接");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject json = JSON.parseObject(message);
        if ("ping".equals(json.getString("type"))) return; // ✅ 心跳包不处理
        // 解析消息
        ChatMessage msg = json.toJavaObject(ChatMessage.class);
        // 保存数据库
        chatService.saveMessage(msg);

        // 推送给接收者（如果在线）
        Session toSession = SESSION_POOL.get(msg.getReceiverId());
        if (toSession != null && toSession.isOpen()) {
            try {
                toSession.getBasicRemote().sendText(JSON.toJSONString(msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 更新会话信息
        chatService.updateSession(msg);
    }

    @OnClose
    public void onClose() {
        if (userId != null) {
            SESSION_POOL.remove(userId);
            System.out.println("❌ 用户 " + userId + " 断开连接");
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
