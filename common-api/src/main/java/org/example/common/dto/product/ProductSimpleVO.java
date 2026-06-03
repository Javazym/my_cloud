package org.example.common.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品信息 VO
 * 用于微服务间传递商品数据（简化版，用于审核列表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSimpleVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 商品ID
     */
    private Long id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 副标题
     */
    private String subName;
    
    /**
     * 主图
     */
    private String image;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 库存
     */
    private Integer stock;
    
    /**
     * 销量
     */
    private Integer soldCount;
    
    /**
     * 商家ID
     */
    private Long merchantId;
    
    /**
     * 商家名称
     */
    private String merchantName;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 上架状态：0-下架，1-上架
     */
    private Integer publishStatus;
    
    /**
     * 审核状态：0-待审核，1-通过，2-驳回
     */
    private Integer auditStatus;
    
    /**
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
