package com.milk.controller.admin;

import com.milk.entity.ChatMessage;
import com.milk.result.Result;
import com.milk.service.ChatService;
import com.milk.service.ChatSessionService;
import com.milk.vo.ChatVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RestController("adminChatController")
@RequestMapping("/admin/chat")
@Slf4j
public class ChatController {
    @Autowired
    private ChatSessionService chatSessionService;
    @Autowired
    private ChatService chatService;

    @GetMapping("/getAllChatUser/{id}")
    public Result<List<ChatVO>> getAllChatUser(@PathVariable Long id){
        List<ChatVO> chatSessionList= chatSessionService.getAllChatUser(id);
        return Result.success(chatSessionList);
    }
    @GetMapping("/history")
    public Result<List<ChatMessage>> getHistory(
            @RequestParam Long userId,
            @RequestParam Long sellerId) {
        List<ChatMessage> list = chatService.getHistory(userId, sellerId);
        return Result.success(list);
    }

}
