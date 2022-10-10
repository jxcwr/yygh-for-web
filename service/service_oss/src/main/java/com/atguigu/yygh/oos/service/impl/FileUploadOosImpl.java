package com.atguigu.yygh.oos.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.yygh.oos.prop.OosProperties;
import com.atguigu.yygh.oos.service.FileUploadOos;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月30日 13:59
 */
@Service
public class FileUploadOosImpl implements FileUploadOos {

    @Autowired
    private OosProperties oosProperties;

    @Override
    public String getCertificatesImageURL(MultipartFile file) {

        String bucketName = oosProperties.getBucketname();
        String endPoint = oosProperties.getEndpoint();
        String keyId = oosProperties.getKeyid();
        String keySecret = oosProperties.getKeysecret();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endPoint, keyId, keySecret);

        //拿到文件名
        String originalFilename = file.getOriginalFilename();
        String fileName = file.getOriginalFilename();
        //生成随机唯一值，使用uuid，添加到文件名称里面
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        fileName = uuid + fileName;
        //按照当前日期，创建文件夹，上传到创建文件夹里面
        //  2021/02/02/01.jpg
        String timeUrl = new DateTime().toString("yyyy/MM/dd");
        fileName = timeUrl + "/" + fileName;
        //调用方法实现上传
        try {
            ossClient.putObject(bucketName, fileName, file.getInputStream());

            //拿到文件在oos服务器上的地址
            // https://yygh-atguigu.oss-cn-beijing.aliyuncs.com/01.jpg
            String url = "https://" + bucketName + "." + endPoint + "/" + fileName;
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            ossClient.shutdown();
        }
    }
}
