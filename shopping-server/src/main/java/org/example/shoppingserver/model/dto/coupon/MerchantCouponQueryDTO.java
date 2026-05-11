package org.example.shoppingserver.model.dto.coupon;

import lombok.Data;

import java.io.Serializable;

/**
 * 商家优惠券查询DTO
 */
@Data
public class MerchantCouponQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券状态（1-可用，2-已停用）
     */
    private Integer status;

    /**
     * 页码，默认1
     */
    private Integer pageNum = 1;

    /**
     * 每页数量，默认10
     */
    private Integer pageSize = 10;
    /**
     * 商家 ID
     */
    private Long merchantId;
}
