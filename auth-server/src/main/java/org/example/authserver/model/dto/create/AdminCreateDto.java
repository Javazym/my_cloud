package org.example.authserver.model.dto.create;

import lombok.Data;

@Data
public class AdminCreateDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String realName;
    private String avatar;
}