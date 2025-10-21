package com.milk.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.milk.constant.MessageConstant;
import com.milk.context.BaseContext;
import com.milk.dto.UserLoginDTO;
import com.milk.dto.UserMsgUpdateDTO;
import com.milk.entity.User;
import com.milk.exception.LoginFailedException;
import com.milk.mapper.UserMapper;
import com.milk.properties.WeChatProperties;
import com.milk.result.Result;
import com.milk.service.UserService;
import com.milk.utils.HttpClientUtil;
import com.milk.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: UserServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/7 23:35
 * @Version 1.0
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        Map<String, String> map=new HashMap<String, String>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        if(openid ==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user=userMapper.getByOpenId(openid);

        if(user==null){
            user=new User();
            user.setOpenid(openid);
            user.setAvatar(userLoginDTO.getAvatar());
            user.setName(userLoginDTO.getName());
            Integer gender = userLoginDTO.getSex();
            String genderStr = "未知";
            if (gender != null) {
                if (gender == 1) genderStr = "男";
                else if (gender == 2) genderStr = "女";
            }
            user.setSex(genderStr);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);
        }
        return user;
    }

    @Override
    public UserVO getMy() {
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
        UserVO userVO=new UserVO();
        BeanUtils.copyProperties(user,userVO);
        userVO.setAvatarUrl(user.getAvatar());
        userVO.setNickName(user.getName());
        return userVO;
    }

    @Override
    public void updateMyMsg(UserMsgUpdateDTO userMsgUpdateDTO) {
        Long userId = BaseContext.getCurrentId();
        User user=new User();
        user.setId(userId);
        user.setName(userMsgUpdateDTO.getNickname());
        user.setSex(userMsgUpdateDTO.getGender());
        user.setPhone(userMsgUpdateDTO.getPhone());
        user.setAvatar(userMsgUpdateDTO.getAvatar());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public Result sendCode(String phone) {
        // 1. 参数校验
        if (phone == null || phone.trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }

        // 2. 手机号格式验证（只允许中国大陆合法手机号）
        if (!isValidPhone(phone)) {
            return Result.error("手机号格式不正确");
        }

        // 3. 限制频繁发送
        String key = "phone:" + phone;
//        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
//            return Result.error("验证码已发送，请稍后再试");
//        }

        // 4. 生成6位验证码
        String code = RandomUtil.randomNumbers(6);

        // 5. 存入Redis（有效期5分钟）
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        // 6. 模拟发送验证码（实际应接入短信服务）
        log.info("【短信验证码】手机号：{}，验证码：{}", phone, code);

        return Result.success("验证码发送成功");
    }


    @Override
    public Result bindPhone(String phone, String code) {
        // 1. 参数校验
        if (phone == null || phone.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            return Result.error("手机号或验证码不能为空");
        }

        // 2. 手机号格式校验
        if (!isValidPhone(phone)) {
            return Result.error("手机号格式不正确");
        }

        // 3. 获取当前用户
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
        if (user == null) {
            return Result.error("用户不存在或未登录");
        }

        // 4. 检查是否已绑定该手机号
        User userByPhone=userMapper.selectByPhone(phone);
        if (userByPhone != null ) {
            return Result.error("该手机号已绑定，请勿重复绑定");
        }

        // 5. 校验验证码
        String key = "phone:" + phone;
        String cacheCode = (String) redisTemplate.opsForValue().get(key);
        if (cacheCode == null) {
            return Result.error("验证码已过期，请重新获取");
        }
        if (!cacheCode.equals(code)) {
            return Result.error("验证码错误");
        }

        // 6. 更新手机号
        user.setPhone(phone);
        userMapper.update(user);

        // 7. 删除验证码，防止重复使用
        redisTemplate.delete(key);

        return Result.success("手机号绑定成功");
    }


    /**
     * 手机号格式校验（中国大陆）
     * 规则：1开头的11位数字，第二位为3-9之间的数
     */
    private boolean isValidPhone(String phone) {
        String regex = "^1[3-9]\\d{9}$";
        return phone != null && phone.matches(regex);
    }


}
