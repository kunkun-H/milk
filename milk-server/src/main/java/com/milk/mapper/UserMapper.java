package com.milk.mapper;

import com.milk.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {


    @Select("SELECT * FROM user WHERE openid = #{openid}")
    User getByOpenId(String openid);

    void insert(User user);

    @Select("SELECT * FROM user WHERE id=#{userId}")
    User getById(Long userId);

    Integer countByMap(Map map);

    void update(User user);

    User selectByPhone(String phone);
}
