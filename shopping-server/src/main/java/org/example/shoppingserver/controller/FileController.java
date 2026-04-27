 package org.example.shoppingserver.controller;


import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.service.impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {
 @Autowired
 private FileServiceImpl fileService;

 /**
  * 上传文件
  *
  * @param file     要上传的文件
  * @param fileName 文件名
  * @return 文件访问路径
  */
 @PostMapping
 public ResponseResult<?> upload(MultipartFile file, String fileName){
     return ResponseResult.success(fileService.uploadFile(file, fileName));
 }
 /**
  * 删除文件
  *
  * @param fileName 要删除的文件名
  * @return 操作结果
  */
 @DeleteMapping
 public ResponseResult<?> delete(String fileName){
     fileService.deleteFile(fileName);
     return ResponseResult.success();
 }
}
