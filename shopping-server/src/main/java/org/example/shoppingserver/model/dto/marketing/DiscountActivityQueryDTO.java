package org.example.shoppingserver.model.dto.marketing;

import lombok.Data;

import java.io.Serializable;

/**
 * 满减活动查询DTO
 */
@Data
public class DiscountActivityQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动状态（0-未开始，1-进行中，2-已结束，3-已取消）
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
