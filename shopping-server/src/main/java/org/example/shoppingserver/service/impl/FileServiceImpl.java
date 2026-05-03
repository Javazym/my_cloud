package org.example.shoppingserver.service.impl;

import org.example.shoppingserver.util.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl {
    @Autowired
    private AliOssUtil aliOssUtil;
    
    /**
     * 上传文件
     * 注意：文件上传通常不使用缓存，因为每次上传都是新文件
     */
    public String uploadFile(MultipartFile file, String fileName) {
        return aliOssUtil.upload(file, fileName);
    }
    
    /**
     * 删除文件
     * 注意：文件删除不使用缓存，直接操作OSS
     */
    public void deleteFile(String objectName) {
        aliOssUtil.deleteFile(objectName);
    }
}
