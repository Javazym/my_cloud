package org.example.shoppingserver.model.vo.marketing;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 轮播图VO
 */
@Data
public class BannerVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 轮播图ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String image;

    /**
     * 图片URL（兼容字段）
     */
    private String imageUrl;

    /**
     * 链接地址
     */
    private String link;

    /**
     * 链接地址（兼容字段）
     */
    private String linkUrl;

    /**
     * 链接类型
     */
    private String linkType;

    /**
     * 位置：0-首页，1-分类页
     */
    private Integer position;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 点击次数
     */
    private Integer clickCount;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
