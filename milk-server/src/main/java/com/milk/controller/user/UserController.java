package com.milk.controller.user;

import com.milk.constant.JwtClaimsConstant;
import com.milk.dto.UserLoginDTO;
import com.milk.dto.UserMsgUpdateDTO;
import com.milk.entity.User;
import com.milk.properties.JwtProperties;
import com.milk.result.Result;
import com.milk.service.UserService;
import com.milk.utils.JwtUtil;
import com.milk.vo.UserLoginVO;
import com.milk.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: UserController
 * Package: com.milk.controller.user
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/7 23:12
 * @Version 1.0
 */
@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(value = "UserController", description = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping ("/login")
    @ApiOperation(value = "微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信登录 {}", userLoginDTO);
        User user= userService.wxLogin(userLoginDTO);
        Map<String, Object> claims=new HashMap<String, Object>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLogin = new UserLoginVO();
        userLogin.setId(user.getId());
        userLogin.setToken(token);
        userLogin.setOpenid(user.getOpenid());
        userLogin.setAvatarUrl(user.getAvatar());
        userLogin.setNickName(user.getName());
        userLogin.setPhone(user.getPhone());
        return Result.success(userLogin);
    }
    @PostMapping("/sendCode")
    public Result sendCode(String phone){
        return userService.sendCode(phone);
    }
    @PostMapping("/bindPhone")
    public Result bindPhone(String phone,String code){
        return userService.bindPhone(phone,code);
    }

    @GetMapping("getMy")
    public Result<UserVO> getMy(){
        UserVO userVO=userService.getMy();
        return Result.success(userVO);
    }
    @PostMapping("updateMy")
    public Result updateMy(@RequestBody UserMsgUpdateDTO userMsgUpdateDTO){
        userService.updateMyMsg(userMsgUpdateDTO);
        return Result.success();
    }
}
