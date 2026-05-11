package org.example.shoppingserver.model.vo.merchant;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家申请VO
 */
@Data
public class MerchantApplicationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 店铺名称
     */
    private String storeName;

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
     * 营业执照图片
     */
    private String businessLicense;

    /**
     * 身份证正面
     */
    private String idCardFront;

    /**
     * 身份证反面
     */
    private String idCardBack;

    /**
     * 审核状态：0-待审核，1-通过，2-驳回
     */
    private Integer status;

    /**
     * 审核状态描述
     */
    private String statusDescription;

    /**
     * 审核备注
     */
    private String remark;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
}
