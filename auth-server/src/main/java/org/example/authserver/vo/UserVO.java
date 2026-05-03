package org.example.authserver.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private String id;
    private String username;
    private String email;
    private String realName;
    private String avatar;
    private int status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> roles;
}