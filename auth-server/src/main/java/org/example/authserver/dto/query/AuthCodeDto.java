package org.example.authserver.dto.query;

import lombok.Data;

@Data
public class AuthCodeDto {
    private String code;
    private String email;
}
