package com.milk.controller.user;

import com.milk.entity.ChatMessage;
import com.milk.result.Result;
import com.milk.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: ChatController
 * Package: com.milk.controller.user
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/13 10:52
 * @Version 1.0
 */
@RestController("userChatController")
@RequestMapping("/user/chat")
@Slf4j
public class ChatController {
    @Autowired
    private ChatService chatService;
    @GetMapping("/history")
    public Result<List<ChatMessage>> getHistory(
            @RequestParam Long userId,
            @RequestParam Long sellerId) {
        List<ChatMessage> list = chatService.getHistory(userId, sellerId);
        return Result.success(list);
    }
}
