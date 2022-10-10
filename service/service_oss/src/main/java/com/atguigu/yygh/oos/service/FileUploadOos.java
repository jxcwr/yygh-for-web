package com.atguigu.yygh.oos.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadOos {
    String getCertificatesImageURL(MultipartFile file);
}
