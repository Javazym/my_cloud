package org.example.authserver.model.dto.update;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String id;
    private String username;
    private String email;
    private Integer status;
}