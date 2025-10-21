package com.milk.service;

import com.milk.vo.ChatVO;

import java.util.List;

/**
 * ClassName: ChatSessionService
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/13 10:57
 * @Version 1.0
 */
public interface ChatSessionService {
    List<ChatVO> getAllChatUser(Long id);
}
