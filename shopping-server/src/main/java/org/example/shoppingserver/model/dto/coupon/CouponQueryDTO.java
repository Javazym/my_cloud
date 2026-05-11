package org.example.shoppingserver.model.dto.coupon;

import lombok.Data;

@Data
public class CouponQueryDTO {
    private Long merchantId;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
