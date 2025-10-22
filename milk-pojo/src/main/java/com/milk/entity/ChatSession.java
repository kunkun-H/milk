package com.milk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: ChatSession
 * Package: com.milk.entity
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/13 10:58
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long sellerId;
    private String lastMessage;
    private LocalDateTime lastTime;
    private Integer unreadCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
