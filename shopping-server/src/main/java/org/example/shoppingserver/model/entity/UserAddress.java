package org.example.shoppingserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 收货地址实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_addresses")
public class UserAddress extends BaseEntity {

    /**
     * 用户ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    /**
     * 收货人姓名
     */
    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    /**
     * 收货人电话
     */
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    /**
     * 省份
     */
    @Column(name = "province", nullable = false, length = 50)
    private String province;

    /**
     * 城市
     */
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    /**
     * 区县
     */
    @Column(name = "district", nullable = false, length = 50)
    private String district;

    /**
     * 详细地址
     */
    @Column(name = "detail_address", nullable = false, length = 255)
    private String detailAddress;

    /**
     * 邮政编码
     */
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /**
     * 是否默认：0-否，1-是
     */
    @Column(name = "is_default")
    private Integer isDefault = 0;

    /**
     * 获取完整收货地址
     */
    public String getFullAddress() {
        return province + city + district + detailAddress;
    }

    /**
     * 获取完整收货信息
     */
    public String getFullReceiverInfo() {
        return receiverName + " " + receiverPhone + " " + getFullAddress();
    }
}
