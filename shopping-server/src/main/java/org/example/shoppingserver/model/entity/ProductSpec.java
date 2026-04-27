package org.example.shoppingserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品规格实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_specs")
public class ProductSpec extends BaseEntity {

    /**
     * 商品ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 规格名称（如：颜色、尺寸）
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 排序
     */
    @Column(name = "sort")
    private Integer sort = 0;

    /**
     * 规格值列表
     */
    @OneToMany(mappedBy = "spec", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSpecValue> values = new ArrayList<>();

    /**
     * 添加规格值
     */
    public void addValue(ProductSpecValue value) {
        values.add(value);
        value.setSpec(this);
    }
}
