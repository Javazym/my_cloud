package org.example.shoppingserver.model.entity.merchant;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.user.User;
import org.example.shoppingserver.model.entity.common.AuditStatus;
import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.util.JsonListConverter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家入驻申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "merchant_applications")
public class MerchantApplication extends BaseEntity {

    /**
     * 用户ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 店铺名称
     */
    @Column(name = "store_name", nullable = false, length = 100)
    private String storeName;

    /**
     * 店铺类型
     */
    @Column(name = "store_type", length = 20)
    private String storeType = "normal";

    /**
     * 主营类目
     */
    @Column(name = "category", length = 50)
@Convert(converter = JsonListConverter.class)
    private List<String> category;

    /**
     * 联系人
     */
    @Column(name = "contact_name", nullable = false, length = 50)
    private String contactName;

    /**
     * 联系电话
     */
    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    /**
     * 营业执照
     */
    @Column(name = "business_license", length = 255)
    private String businessLicense;

    /**
     * 身份证正面
     */
    @Column(name = "id_card_front", length = 255)
    private String idCardFront;

    /**
     * 身份证背面
     */
    @Column(name = "id_card_back", length = 255)
    private String idCardBack;

    /**
     * 审核状态
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private AuditStatus status = AuditStatus.PENDING;

    /**
     * 审核备注
     */
    @Column(name = "remark", length = 255)
    private String remark;

    /**
     * 申请时间
     */
    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;
}
