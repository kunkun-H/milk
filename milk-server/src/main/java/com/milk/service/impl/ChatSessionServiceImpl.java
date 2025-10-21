package com.milk.service.impl;

import com.milk.entity.ChatSession;
import com.milk.entity.User;
import com.milk.mapper.ChatSessionMapper;
import com.milk.mapper.UserMapper;
import com.milk.service.ChatSessionService;
import com.milk.vo.ChatVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: ChatSessionServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/13 11:00
 * @Version 1.0
 */
@Service
public class ChatSessionServiceImpl implements ChatSessionService {
    @Autowired
    private ChatSessionMapper chatSessionMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<ChatVO> getAllChatUser(Long id) {
        List<ChatSession> chatSessionList= chatSessionMapper.findAllBySeller(id);
        List<ChatVO> list=new ArrayList<>();
        if(chatSessionList!=null){
            for(ChatSession chatSession:chatSessionList){
                ChatVO chatVO=new ChatVO();
                BeanUtils.copyProperties(chatSession,chatVO);
                Long userId= chatSession.getUserId();
                if(userId!=null){
                    User user = userMapper.getById(userId);
                    if(user!=null){
                        chatVO.setName(user.getName());
                        chatVO.setAvatar(user.getAvatar());
                    }
                }
                list.add(chatVO);
            }
        }
        return list;
    }
}
