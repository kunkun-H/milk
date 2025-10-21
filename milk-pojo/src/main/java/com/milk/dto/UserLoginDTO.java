package com.milk.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
public class UserLoginDTO implements Serializable {

    private String code;
    private String avatar;
    private String name;
    private Integer sex;
}
