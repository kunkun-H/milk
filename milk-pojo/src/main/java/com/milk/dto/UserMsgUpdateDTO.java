package com.milk.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
public class UserMsgUpdateDTO implements Serializable {
    private String avatar;
    private String nickname;
    private String phone;
    private String gender;
}
