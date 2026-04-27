package org.example.shoppingserver.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "ali.oss")
@Data
public class AliOssUtil {
    @Value("${ali.oss.endpoint}")
    private String endpoint;
    @Value("${ali.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${ali.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${ali.oss.bucketName}")
    private String bucketName;

    // 上传字节数组到OSS
    public String upload(MultipartFile bytes, String objectName) {
        // 1. 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String name = generateUniqueFileName(objectName);
        try {
            // 2. 执行上传
            ossClient.putObject(bucketName, name, new ByteArrayInputStream(bytes.getBytes()));
        } catch (Exception e) {
            // 异常处理
            throw new RuntimeException("文件上传失败", e);
        } finally {
            // 3. 关闭client
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 4. 返回文件访问URL
        return "https://" + bucketName + "." + endpoint + "/" + name;
    }
    public void deleteFile(String objectName) {
        // 直接删除，对于开启版本控制的Bucket，这会创建一个删除标记
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteObject(bucketName, objectName);
        // 如果需要彻底删除特定版本（需提供 versionId）
        // ossClient.deleteObject(new DeleteObjectRequest(bucketName, objectName).withVersionId("具体版本ID"));
    }
    public String generateUniqueFileName(String originalFileName) {
        // 获取文件后缀
        String fileExtension = "";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            fileExtension = originalFileName.substring(lastDotIndex);
        }

        // 使用 UUID 生成唯一前缀，保证文件名全局唯一
        String uniquePrefix = UUID.randomUUID().toString();

        // 组合新文件名，例如: "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8.jpg"
        return uniquePrefix + fileExtension;
    }
}