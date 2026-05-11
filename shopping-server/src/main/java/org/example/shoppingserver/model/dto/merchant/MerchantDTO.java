package org.example.shoppingserver.model.dto.merchant;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商家DTO
 */
@Data
public class MerchantDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商家ID
     */
    private Long id;

    /**
     * 用户ID
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
     * 店铺类型
     */
    private String storeType;

    /**
     * 主营类目
     */
    private List<String> category;

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
     * 状态
     */
    private Integer status;

    /**
     * 审核状态
     */
    private Integer auditStatus;
}
