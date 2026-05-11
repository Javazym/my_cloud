package org.example.shoppingserver.model.vo.merchant;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商家VO
 */
@Data
public class MerchantVO implements Serializable {

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
     * 店铺描述
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
     * 联系人姓名
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
     * 评分
     */
    private BigDecimal rating;

    /**
     * 销量
     */
    private Long sales;

    /**
     * 粉丝数
     */
    private Integer followers;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 审核状态：0-待审核，1-通过，2-驳回
     */
    private Integer auditStatus;
}
