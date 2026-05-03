package org.example.shoppingserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 轮播图实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "banners")
public class Banner extends BaseEntity {

    /**
     * 标题
     */
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /**
     * 图片URL
     */
    @Column(name = "image", nullable = false, length = 255)
    private String image;

    /**
     * 图片URL（别名，兼容DTO）
     */
    @Transient
    private String imageUrl;

    /**
     * 链接地址
     */
    @Column(name = "link", length = 255)
    private String link;

    /**
     * 链接地址（别名，兼容DTO）
     */
    @Transient
    private String linkUrl;

    /**
     * 链接类型：product-商品，category-分类，url-网页
     */
    @Column(name = "link_type", length = 20)
    private String linkType;

    /**
     * 位置：0-首页，1-分类页
     */
    @Column(name = "position")
    private Integer position = 0;

    /**
     * 排序
     */
    @Column(name = "sort")
    private Integer sort = 0;

    /**
     * 状态：0-禁用，1-启用
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 点击次数
     */
    @Column(name = "click_count")
    private Integer clickCount = 0;

    /**
     * 增加点击次数
     */
    public void incrementClickCount() {
        this.clickCount++;
    }
}
