package org.example.shoppingserver.model.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 退款DTO
 */
@Data
public class RefundDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 退款类型：0-仅退款，1-退货退款
     */
    @NotNull(message = "退款类型不能为空")
    private Integer type;

    /**
     * 退款金额
     */
    @NotNull(message = "退款金额不能为空")
    @DecimalMin(value = "0.01", message = "退款金额必须大于0")
    private BigDecimal amount;

    /**
     * 退款原因
     */
    @NotBlank(message = "退款原因不能为空")
    private String reason;

    /**
     * 退款描述
     */
    private String description;

    /**
     * 图片凭证（JSON数组）
     */
    private String images;
}
