package com.milk.aspect;

import com.milk.annotation.AutoFill;
import com.milk.constant.AutoFillConstant;
import com.milk.context.BaseContext;
import com.milk.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * ClassName: AutoFillAspect
 * Package: com.milk.aspect
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/2 0:45
 * @Version 1.0
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.milk.mapper.*.*(..)) && @annotation(com.milk.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始填充参数：{}", joinPoint.getSignature().getName());
        MethodSignature signature=(MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = autoFill.value();//获得数据库的操作类型

        Object[] args = joinPoint.getArgs();
        if(args==null || args.length==0){
            return;
        }
        Object entity=args[0];

        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        if(value==OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(value==OperationType.UPDATE){
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
