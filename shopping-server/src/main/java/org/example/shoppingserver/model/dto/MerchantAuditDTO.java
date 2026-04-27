package org.example.shoppingserver.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商家审核请求DTO
 */
@Data
public class MerchantAuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID或商家ID
     */
    private Long id;

    /**
     * 审核状态：1-通过，2-驳回
     */
    private Integer status;

    /**
     * 审核备注
     */
    private String remark;
}
