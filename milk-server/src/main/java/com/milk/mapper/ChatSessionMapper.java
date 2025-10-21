package com.milk.mapper;

import com.milk.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: ChatSessionMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/13 11:02
 * @Version 1.0
 */
@Mapper
public interface ChatSessionMapper {
    ChatSession findByUserAndSeller(Long userId, Long sellerId);

    void insert(ChatSession session);

    void update(ChatSession session);

    List<ChatSession> findAllBySeller(Long id);
}
