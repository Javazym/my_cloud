package org.example.authserver.model.dto.query;

import lombok.Data;

@Data
public class UserQueryDto {
    private String username;
    private String email;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}