package com.milk.config;

import com.milk.properties.AliOssProperties;
import com.milk.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: OssConfiguration
 * Package: com.milk.config
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/3 0:57
 * @Version 1.0
 */
@Configuration
@Slf4j
public class OssConfiguration {
    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象");
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
