package com.atguigu.yygh.oos.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.oos.service.FileUploadOos;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月30日 12:42
 */
@Api(tags = "头像上传相关接口")
@RestController
@RequestMapping("/admin/oss/file")
public class FileUploadController {

    @Autowired
    private FileUploadOos fileUploadOos;

    @ApiOperation(value = "文件上传")
    @PostMapping("upload")
    public R upload(
            @ApiParam(name = "file", value = "上传的文件", required = true)
            @RequestParam("file") MultipartFile file) {
        String url = fileUploadOos.getCertificatesImageURL(file);
        return R.ok().message("文件上传成功").data("url", url);
    }
}
