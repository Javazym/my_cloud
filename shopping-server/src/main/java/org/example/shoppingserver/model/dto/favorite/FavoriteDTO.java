package org.example.shoppingserver.model.dto.favorite;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏DTO
 */
@Data
public class FavoriteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private String merchantName;
    private LocalDateTime createdAt;
}
