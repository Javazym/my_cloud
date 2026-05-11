package org.example.shoppingserver.model.vo.favorite;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏VO
 */
@Data
public class FavoriteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 商品价格
     */
    private BigDecimal productPrice;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
