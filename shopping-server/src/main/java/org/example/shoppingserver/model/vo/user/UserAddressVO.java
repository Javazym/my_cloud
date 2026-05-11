package org.example.shoppingserver.model.vo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户收货地址VO
 */
@Data
public class UserAddressVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    private Long id;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 是否默认：0-否，1-是
     */
    private Integer isDefault;

    /**
     * 完整收货地址
     */
    private String fullAddress;
}
