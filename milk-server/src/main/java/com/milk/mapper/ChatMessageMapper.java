package com.milk.mapper;

import com.milk.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: ChatMessageMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/13 11:01
 * @Version 1.0
 */
@Mapper
public interface ChatMessageMapper {


     List<ChatMessage> getAllMsg(Long senderId, Long receiverId) ;

    void insert(ChatMessage msg);
}
