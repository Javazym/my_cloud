package org.example.shoppingserver.service.impl;

import org.example.shoppingserver.util.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl {
    @Autowired
    private AliOssUtil aliOssUtil;
    public String uploadFile(MultipartFile file, String fileName) {
        return aliOssUtil.upload(file, fileName);
    }
    public void deleteFile(String objectName) {
        aliOssUtil.deleteFile(objectName);
    }
}
