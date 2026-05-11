package org.example.shoppingserver.model.dto.admin;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员DTO
 */
@Data
public class AdminDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private Integer status;
    private LocalDateTime lastLoginTime;
}
