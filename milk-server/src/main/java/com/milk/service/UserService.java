package com.milk.service;

import com.milk.dto.UserLoginDTO;
import com.milk.dto.UserMsgUpdateDTO;
import com.milk.entity.User;
import com.milk.result.Result;
import com.milk.vo.UserVO;

/**
 * ClassName: UserService
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/7 23:19
 * @Version 1.0
 */
public interface UserService {
    User wxLogin(UserLoginDTO userLoginDTO);

    UserVO getMy();

    void updateMyMsg(UserMsgUpdateDTO userMsgUpdateDTO);

    Result sendCode(String phone);

    Result bindPhone(String phone, String code);
}
