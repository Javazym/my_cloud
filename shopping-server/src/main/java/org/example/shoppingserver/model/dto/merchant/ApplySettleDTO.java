package org.example.shoppingserver.model.dto.merchant;

import lombok.Data;

import java.util.List;

/**
 * 商家入驻申请DTO
 */
@Data
public class ApplySettleDTO {
    /**
     * 店铺名称
     */
    private String storeName;
    
    /**
     * 店铺类型
     */
    private String storeType;
    
    /**
     * 经营类目
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
}
