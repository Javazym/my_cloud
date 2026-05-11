package org.example.shoppingserver.model.entity.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.common.BaseEntity;

/**
 * 商品规格值实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_spec_values")
public class ProductSpecValue extends BaseEntity {

    /**
     * 规格ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false)
    private ProductSpec spec;

    /**
     * 规格值
     */
    @Column(name = "value", nullable = false, length = 50)
    private String value;

    /**
     * 排序
     */
    @Column(name = "sort")
    private Integer sort = 0;
}
