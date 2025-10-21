package com.milk.controller.admin;

import com.milk.constant.MessageConstant;
import com.milk.result.Result;
import com.milk.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * ClassName: CommonController
 * Package: com.milk.controller.admin
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/3 0:04
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String ObjectName= UUID.randomUUID().toString()+suffix;
        try {
            String filePath=aliOssUtil.upload(file.getBytes(), ObjectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败：{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
