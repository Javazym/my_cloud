package org.example.authserver.model.dto.create;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String code;
    private String email;
}
