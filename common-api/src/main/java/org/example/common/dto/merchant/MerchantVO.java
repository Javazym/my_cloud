package org.example.common.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户信息 VO
 * 用于微服务间传递商户数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 商户ID
     */
    private Long id;
    
    /**
     * 关联用户ID
     */
    private String userId;
    
    /**
     * 店铺名称
     */
    private String storeName;
    
    /**
     * 店铺Logo
     */
    private String storeLogo;
    
    /**
     * 店铺横幅
     */
    private String storeBanner;
    
    /**
     * 店铺简介
     */
    private String storeDescription;
    
    /**
     * 店铺类型：normal-普通，flagship-旗舰店
     */
    private String storeType;
    
    /**
     * 联系人
     */
    private String contactName;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 联系邮箱
     */
    private String contactEmail;
    
    /**
     * 店铺评分
     */
    private BigDecimal rating;
    
    /**
     * 总销售额
     */
    private Long sales;
    
    /**
     * 关注数
     */
    private Integer followers;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
